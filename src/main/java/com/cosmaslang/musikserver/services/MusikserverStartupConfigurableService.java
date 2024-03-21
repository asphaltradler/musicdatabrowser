package com.cosmaslang.musikserver.services;

import com.cosmaslang.musikserver.MusikScanner;
import com.cosmaslang.musikserver.db.entities.*;
import com.cosmaslang.musikserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikserver.db.repositories.TrackRepository;
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

    private Track createTrack(AudioFile audioFile) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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

        try {
	        Tag tag = audioFile.getTag();
	        if (tag == null) {
	        	track.setTitle(file.getName());
	        } else {
		        String title;
		        String str = tag.getFirst(FieldKey.TITLE);
		        if (tagValueIsValid(str)) {
		            title = str;
		        } else {
		            title = file.getName();
		        }
		        track.setTitle(title);
		        str = tag.getFirst(FieldKey.TRACK);
		        if (tagValueIsValid(str)) {
		            try {
						track.setTracknumber(Integer.valueOf(str));
					} catch (NumberFormatException e) {
						logger.log(Level.WARNING, "invalid track number " + str + " for track " + path);
					}
		        }
		        str = tag.getFirst(FieldKey.ALBUM);
		        if (tagValueIsValid(str)) {
		            Album album = createEntity(Album.class, albumRepository, str);
		            track.setAlbum(album);
		        }
		        str = tag.getFirst(FieldKey.COMPOSER);
		        if (tagValueIsValid(str)) {
		            Komponist komponist = createEntity(Komponist.class, komponistRepository, str);
		            track.setKomponist(komponist);
		        }
		        str = tag.getFirst(Track.FIELDKEY_WORK);
		        if (tagValueIsValid(str)) {
		            Werk werk = createEntity(Werk.class, werkRepository, str);
		            track.setWerk(werk);
		        }
		        //ManyToMany Zuordnung
		        List<TagField> tagFields = tag.getFields(FieldKey.ARTIST);
		        if (tagFields != null) {
		            for (TagField field : tagFields) {
		            	str = field.toString();
		            	if (tagValueIsValid(str)) {
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
		            	if (tagValueIsValid(str)) {
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
	
	        //technical data
	        track.setSize(file.length());
	        AudioHeader header = audioFile.getAudioHeader();
	        if (header != null) {
		        track.setBitsPerSample(header.getBitsPerSample());
		        track.setSamplerate(header.getSampleRateAsNumber());
		        track.setEncoding(header.getFormat());
		        track.setLengthInSeconds(header.getTrackLength());
		        //ist null bei Ogg, WMA, manchen WAV, etc.
		        track.setNoOfSamples(header.getNoOfSamples());
		        //Long length = header.getAudioDataLength();
		        String hash;
		        //nur Flac hat einen korrekten Audio-MD5, der unabhängig von Tags ist
		        if (header instanceof FlacAudioHeader) {
		        	hash = ((FlacAudioHeader)header).getMd5();
		        	//TODO wenn 0000... (md5 fehlt in flac)
		        	//char[] hexChars = new char[32]
		        } else {
		        	//beim Rest muss filepath-hash plus Länge in samples reichen, um Änderungen anzuzeigen 
					hash = Long.toHexString(path.hashCode())
							+ '-' + header.getNoOfSamples();
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
		    	track.setHash(hash);
	        }
        } catch (Throwable t) {
        	logger.log(Level.WARNING, "error when processing track " + path);
        }
        return track;
    }
    
    private boolean tagValueIsValid(String str) {
    	return str != null && str.length() > 0;
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
