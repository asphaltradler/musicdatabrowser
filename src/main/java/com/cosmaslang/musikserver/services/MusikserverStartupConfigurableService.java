package com.cosmaslang.musikserver.services;

import com.cosmaslang.musikserver.MusikScanner;
import com.cosmaslang.musikserver.db.entities.*;
import com.cosmaslang.musikserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikserver.db.repositories.TrackRepository;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
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
            logger.warning(e.getMessage());
        }
        logger.info("processed " + audioFile.getFile().getName());
    }

    private Track createTrack(AudioFile audioFile) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Path filepath = audioFile.getFile().toPath();
        String path = filepath.subpath(rootPathSteps, filepath.getNameCount()).toString().replace('\\', '/');
        Track track = trackRepository.findByPath(path);
        if (track == null) {
            track = new Track();
            track.setPath(path);
            logger.info("created new track " + path);
        } else {
            logger.info("-    update track " + path);
        }

        Tag tag = audioFile.getTag();

        String title;
        TagField tagField = tag.getFirstField(FieldKey.TITLE);
        if (tagField == null) {
            title = audioFile.getFile().getName();
        } else {
            title = tagField.toString();
        }
        track.setTitle(title);
        tagField = tag.getFirstField(FieldKey.TRACK);
        if (tagField != null) {
            track.setTracknumber(Integer.valueOf(tagField.toString()));
        }
        tagField = tag.getFirstField(FieldKey.ALBUM);
        if (tagField != null) {
            Album album = createEntity(Album.class, albumRepository, tagField.toString());
            track.setAlbum(album);
        }
        //TODO many2many
        tagField = tag.getFirstField(FieldKey.GENRE);
        if (tagField != null) {
            Genre genre = createEntity(Genre.class, genreRepository, tagField.toString());
            track.setGenre(genre);
        }
        tagField = tag.getFirstField(FieldKey.COMPOSER);
        if (tagField != null) {
            Komponist komponist = createEntity(Komponist.class, komponistRepository, tagField.toString());
            track.setKomponist(komponist);
        }
        tagField = tag.getFirstField("WORK");
        if (tagField != null) {
            Werk werk = createEntity(Werk.class, werkRepository, tagField.toString());
            track.setWerk(werk);
        }
        //ManyToMany Zuordnung
        List<TagField> tagFields = tag.getFields(FieldKey.ARTIST);
        if (tagFields != null) {
            for (TagField field : tagFields) {
                Interpret interpret = createEntity(Interpret.class, interpretRepository, field.toString());
                track.addInterpret(interpret);
            }
        }

        track.setComment(tag.getFirst(FieldKey.COMMENT));
        track.setPublisher(tag.getFirst("ORGANIZATION"));

        AudioHeader header = audioFile.getAudioHeader();
        track.setBitsPerSample(header.getBitsPerSample());
        track.setSamplerate(header.getSampleRateAsNumber());
        track.setEncoding(header.getEncodingType());
        track.setLengthInSeconds(header.getTrackLength());

        return track;
    }

    /**
     * Generische Erzeugung der richtigen Klasse, falls in der zugeordneten Repository nicht gefunden.
     * Leider nicht möglich, das Repository direkt aus der Klasse herzuleiten
     */
    private <T extends NamedEntity> T createEntity(Class<T> clazz, NamedEntityRepository<T> repo,
                                                   String name) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

    @Override
    public String getRootDir() {
        return rootDirFile.getName();
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
