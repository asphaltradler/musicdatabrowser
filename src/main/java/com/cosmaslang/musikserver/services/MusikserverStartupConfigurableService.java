package com.cosmaslang.musikserver.services;

import com.cosmaslang.musikserver.MusikScanner;
import com.cosmaslang.musikserver.db.entities.*;
import com.cosmaslang.musikserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikserver.db.repositories.TrackRepository;
import io.micrometer.common.util.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.FlacAudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Qualifier("musikserverStartup")
public class MusikserverStartupConfigurableService implements MusikserverStartupService, MusikScanner.AudioFileProcessor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    ConfigurableApplicationContext context;
    @Autowired
    TrackRepository trackRepository;
    @Autowired
    NamedEntityRepository<Interpret> interpretRepository;
    @Autowired
    NamedEntityRepository<Album> albumRepository;
    @Autowired
    NamedEntityRepository<Werk> werkRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;
    @Autowired
    NamedEntityRepository<Komponist> komponistRepository;

    private File rootDirFile;
    private int rootPathSteps;

    public void scanMusikdirectory() throws FileNotFoundException {
        rootPathSteps = rootDirFile.toPath().getNameCount();
        MusikScanner scanner = new MusikScanner(this, rootDirFile);

        logger.info("Scanner found " + scanner.getCount() + " tracks");
        logger.info("              " + scanner.getFailed() + " failed");
    }

    @Override
    public void processAudioFile(AudioFile audioFile) {
        try {
            Track track = createTrack(audioFile);
            trackRepository.save(track);
        } catch (Exception e) {
            logger.log(Level.WARNING, "when scanning " + audioFile.getFile().getName(), e);
        }
        logger.info("processed " + audioFile.getFile().getName());
    }

    private Track createTrack(AudioFile audioFile) {
    	File file = audioFile.getFile();
        Path filepath = file.toPath();
        //path unabhängig von Filesystem notieren, ausgehend von rootDir
        String path = filepath.subpath(rootPathSteps, filepath.getNameCount()).toString().replace('\\', '/');
        Track track = trackRepository.findByPath(path);
        if (track == null) {
            track = new Track();
            track.setPath(path);
            logger.info("create new track " + path);
        } else {
            logger.info("-   update track " + path);
        }

        Tag tag = null;
        try {
	        tag = audioFile.getTag();
	        if (tag == null) {
	        	track.setTitle(file.getName());
	        } else {
		        String title;
		        String str = tag.getFirst(FieldKey.TITLE);
		        if (StringUtils.isNotBlank(str)) {
		            title = str;
		        } else {
		            title = file.getName();
		        }
		        track.setTitle(title);
		        str = tag.getFirst(FieldKey.TRACK);
		        if (StringUtils.isNotBlank(str)) {
		            try {
						track.setTracknumber(Integer.valueOf(str));
					} catch (NumberFormatException e) {
						logger.log(Level.WARNING, "invalid track number " + str + " for track " + path);
					}
		        }
		        str = tag.getFirst(FieldKey.ALBUM);
		        if (StringUtils.isNotBlank(str)) {
		            Album album = createEntity(Album.class, albumRepository, str);
		            track.setAlbum(album);
		        }
		        str = tag.getFirst(FieldKey.COMPOSER);
		        if (StringUtils.isNotBlank(str)) {
		            Komponist komponist = createEntity(Komponist.class, komponistRepository, str);
		            track.setKomponist(komponist);
		        }
		        str = tag.getFirst(Track.FIELDKEY_WORK);
		        if (StringUtils.isNotBlank(str)) {
		            Werk werk = createEntity(Werk.class, werkRepository, str);
		            track.setWerk(werk);
		        }
		        //ManyToMany Zuordnung
		        List<TagField> tagFields = tag.getFields(FieldKey.ARTIST);
		        if (tagFields != null) {
		            for (TagField field : tagFields) {
		            	str = field.toString();
		            	if (StringUtils.isNotBlank(str)) {
			                Interpret interpret = createEntity(Interpret.class, interpretRepository, str);
			                track.addInterpret(interpret);
		            	}
		            }
		        }
		        //ManyToMany Zuordnung
		        tagFields = tag.getFields(FieldKey.GENRE);
		        if (tagFields != null) {
		            for (TagField field : tagFields) {
		            	str = field.toString();
		            	if (StringUtils.isNotBlank(str)) {
			                Genre genre = createEntity(Genre.class, genreRepository, str);
			                track.addGenre(genre);
		            	}
		            }
		        }
		
		        //TODO comment länger als 255?
		        String comment = tag.getFirst(FieldKey.COMMENT);
		        track.setComment(comment.substring(0, Math.min(255, comment.length())));
		        track.setPublisher(tag.getFirst(Track.FIELDKEY_ORGANIZATION));
	        }
        } catch (Throwable t) {
            logger.log(Level.WARNING, "error when processing track " + path
                + " with tag " + (tag == null ? "NULL" : tag), t);
        }

        track.setSize(file.length());
        AudioHeader header = null;
        try {
	        //technical data
	        header = audioFile.getAudioHeader();
	        if (header != null) {
		        track.setBitsPerSample(header.getBitsPerSample());
                track.setBitrate(header.getBitRateAsNumber());
		        track.setSamplerate(header.getSampleRateAsNumber());
		        track.setEncoding(header.getFormat());
		        track.setLengthInSeconds(header.getTrackLength());
		        //ist null bei Ogg, WMA, manchen WAV, etc.
                Long noOfSamples = header.getNoOfSamples();
                if (noOfSamples == null) {
                    //dann selbst berechnen (wieso macht das jaudiotagger nicht schon?)
                    noOfSamples = ((long) track.getLengthInSeconds() * track.getSamplerate());
                }
		        track.setNoOfSamples(noOfSamples);
		        //Long length = header.getAudioDataLength();
                track.setHash(getHash(header, path, track));
	        }
        } catch (Throwable t) {
        	logger.log(Level.WARNING, "error when processing track " + path
                + " with header " +  (header == null ? "NULL" : header));
        }
        return track;
    }

    private static String getHash(AudioHeader header, String path, Track track) {
        //nur Flac hat (meistens) einen korrekten Audio-MD5, der unabhängig von Tags ist
        if (header instanceof FlacAudioHeader) {
            String hash = ((FlacAudioHeader) header).getMd5();
            //fehlerhafte Flacs haben 0-MD5
            if (!containsOnlyZero(hash)) {
                 return hash;
            }
        }
        //beim Rest muss filepath-hash plus Länge in samples reichen, um Änderungen anzuzeigen
        return getFileHash(path, track);
        //Leider viel zu langsam...
        /*
        try {
            HashCode md5 = Files.asByteSource(file).hash(Hashing.md5());
            hash = md5.toString();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Can't hash " + path, e);
            hash = Long.toHexString(path.hashCode())
                    + '-' + header.getNoOfSamples().toString();
        }
        */
    }

    private static boolean containsOnlyZero(String hash) {
        if (hash != null) {
            for (int i = 0; i < hash.length(); i++) {
                if (hash.charAt(i) != '0') {
                    return false;
                }
            }
        }
        return true;
    }

    private static String getFileHash(String path, Track track) {
        return Long.toHexString(path.hashCode())
                + '-' + Long.toHexString(track.getNoOfSamples());
    }

    /**
     * Generische Erzeugung einer Entity der richtigen Klasse, falls in der zugeordneten Repository nicht gefunden.
     * Leider nicht möglich, das Repository direkt aus der Klasse herzuleiten.
     * Behebt gleichzeitig einige Ungereimtheiten in den Formaten der tags.
     */
    private <T extends NamedEntity> T createEntity(Class<T> clazz, NamedEntityRepository<T> repo,
                                                   String name) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (name.toLowerCase().startsWith("text=\"")) {
        	name = name.substring(6, name.length() -1).trim();
        }
        //TODO wieso kommen hier 0-bytes mitten im String vor?
        int pos = name.indexOf((char)0);
        if (pos > -1) {
        	name = name.substring(0, pos);
        }
        //TODO auch " kommen manchmal am Ende vor
        pos = name.lastIndexOf('"');
        if (pos > 0) {
        	name = name.substring(0, pos);
        }
    	T entity = repo.findByName(name);
        if (entity == null) {
            entity = clazz.getDeclaredConstructor().newInstance();
            entity.setName(name);
            repo.save(entity);
        }
        return entity;
    }

    public void listAllTracks() {
        TrackRepository trackRepository = context.getBean(TrackRepository.class);
        Iterable<Track> allTracks = trackRepository.findAll();
        allTracks.forEach(System.out::println);
        System.out.printf("MusikRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Werke, %d Genres\n",
                trackRepository.count(), albumRepository.count(), komponistRepository.count(), werkRepository.count(), genreRepository.count());
    }

    /**
     * Wird von der {@link com.cosmaslang.musikserver.configuration.MusikserverConfiguration} gesetzt
     */
    @Override
    public void setRootDir(String filename) {
        rootDirFile = new File(filename);
    }

    @Override
    public void start() {
        try {
            scanMusikdirectory();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        listAllTracks();
    }

}
