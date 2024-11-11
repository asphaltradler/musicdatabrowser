package com.cosmaslang.musikdataserver;

import com.cosmaslang.musikdataserver.db.entities.*;
import com.cosmaslang.musikdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikdataserver.services.MusikDataServerStartupService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.PersistenceException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.SupportedFileFormat;
import org.jaudiotagger.audio.flac.FlacAudioHeader;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MusikScanner {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final static List<String> audioFileExtensions = Stream.of(SupportedFileFormat.values()).map(SupportedFileFormat::getFilesuffix).toList();

    private final MusikDataServerStartupService musikDataServerStartupService;

    private int rootPathSteps;
    private long count = 0;
    private long created = 0;
    private long updated = 0;
    private long failed = 0;

    public MusikScanner(MusikDataServerStartupService service) {
        this.musikDataServerStartupService = service;
    }

    //@Transactional
    public void scan(Path rootPath) throws IOException {
        rootPathSteps = rootPath.getNameCount();
        logger.info("Scanning " + rootPath);
        scanDirectory(rootPath);

        logger.info(String.format("Found %d tracks", count));
        logger.info(String.format("  created/updated/failed tracks: %d/%d/%d", created, updated, failed));
    }

    private void scanDirectory(final Path dir) throws IOException {
        try (Stream<Path> audioPaths = Files.list(dir)) {
            //nicht .parallel(): Parallelisierung führt zu Problemen bei lazy initialization
            audioPaths.forEach(path -> {
                try {
                    if (Files.isDirectory(path)) {
                        scanDirectory(path);
                    } else {
                        String ext = Utils.getExtension(path.toFile()).toLowerCase();
                        if (audioFileExtensions.contains(ext)) {
                            count++;
                            try {
                                AudioFile audioFile = AudioFileIO.read(path.toFile());
                                //logger.info("processed " + file.getName());
                                processAudioFile(audioFile);
                            } catch (PersistenceException e) {
                                failed++;
                                throw e;
                            } catch (Throwable t) {
                                failed++;
                                logger.log(Level.WARNING, "Unable to read record:" + count + ":" + path, t);
                            }

                        }
                    }
                } catch (IOException e) {
                    failed++;
                    logger.log(Level.WARNING, "Unable to read: " + path);
                }
            });
        }
    }

    @Transactional
    protected void processAudioFile(AudioFile audioFile) {
        try {
            Track track = createOrUpdateTrack(audioFile);
            musikDataServerStartupService.getTrackRepository().save(track);
            logger.info("processed " + audioFile.getFile().getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "when scanning " + audioFile.getFile().getName(), e);
            throw e;
        }
    }

    private Track createOrUpdateTrack(AudioFile audioFile) {
        File file = audioFile.getFile();
        Path filepath = file.toPath();
        //path unabhängig von Filesystem notieren, ausgehend von rootDir
        String path = filepath.subpath(rootPathSteps, filepath.getNameCount()).toString().replace('\\', '/');
        Track track = musikDataServerStartupService.getTrackRepository().findByPath(path);
        if (track == null) {
            track = new Track();
            track.setPath(path);
            logger.info("create new track " + path);
            created++;
        } else {
            logger.info("-   update track " + path);
            updated++;
        }

        Tag tag = null;
        try {
            tag = audioFile.getTag();
            if (tag == null) {
                track.setName(file.getName());
            } else {
                String title;
                String str = tag.getFirst(FieldKey.TITLE);
                if (StringUtils.isNotBlank(str)) {
                    title = str;
                } else {
                    title = file.getName();
                }
                track.setName(title);
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
                    Album album = createOrUpdateEntity(Album.class, musikDataServerStartupService.getAlbumRepository(), str);
                    track.setAlbum(album);
                    //album.getTracks().add(track);
                }
                str = tag.getFirst(FieldKey.COMPOSER);
                if (StringUtils.isNotBlank(str)) {
                    Komponist komponist = createOrUpdateEntity(Komponist.class, musikDataServerStartupService.getKomponistRepository(), str);
                    track.setKomponist(komponist);
                }
                str = tag.getFirst(Track.FIELDKEY_WORK);
                if (StringUtils.isNotBlank(str)) {
                    Werk werk = createOrUpdateEntity(Werk.class, musikDataServerStartupService.getWerkRepository(), str);
                    track.setWerk(werk);
                }
                //ManyToMany Zuordnung
                List<TagField> tagFields = tag.getFields(FieldKey.ARTIST);
                if (tagFields != null) {
                    //alle als Liste setzen => dann ist kein update eines vorhandenen tracks möglich
                    Set<Interpret> interpreten = new HashSet<>();
                    for (TagField field : tagFields) {
                        str = field.toString();
                        if (StringUtils.isNotBlank(str)) {
                            Interpret interpret = createOrUpdateEntity(Interpret.class, musikDataServerStartupService.getInterpretRepository(), str);
                            //track.addInterpret(interpret);
                            interpreten.add(interpret);
                        }
                    }
                    track.setInterpreten(interpreten);
                }
                //ManyToMany Zuordnung
                tagFields = tag.getFields(FieldKey.GENRE);
                if (tagFields != null) {
                    Set<Genre> genres = new HashSet<>();
                    for (TagField field : tagFields) {
                        str = field.toString();
                        if (StringUtils.isNotBlank(str)) {
                            Genre genre = createOrUpdateEntity(Genre.class, musikDataServerStartupService.getGenreRepository(), str);
                            //track.addGenre(genre);
                            genres.add(genre);
                        }
                    }
                    track.setGenres(genres);
                }

                //TODO comment länger als 255?
                String comment = tag.getFirst(FieldKey.COMMENT);
                track.setComment(comment.substring(0, Math.min(255, comment.length())));
                track.setPublisher(tag.getFirst(Track.FIELDKEY_ORGANIZATION));
            }
        } catch (PersistenceException e) {
            throw e;
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
        } catch (PersistenceException e) {
            throw e;
        } catch (Throwable t) {
            logger.log(Level.WARNING, "error when processing track " + path
                    + " with header " + (header == null ? "NULL" : header));
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
     * Ansonsten Wiederverwendung aus der {@link Repository}
     * Leider nicht möglich, das Repository direkt aus der Klasse herzuleiten.
     * Behebt gleichzeitig einige Ungereimtheiten in den Formaten der tags.
     */
    private <ENTITY extends NamedEntity> ENTITY createOrUpdateEntity(Class<ENTITY> clazz, NamedEntityRepository<ENTITY> repo,
                                                                     String name) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (name.toLowerCase().startsWith("text=\"")) {
            name = name.substring(6, name.length() - 1).trim();
        }
        //TODO wieso kommen hier 0-bytes mitten im String vor?
        int pos = name.indexOf((char) 0);
        if (pos > -1) {
            name = name.substring(0, pos);
        }
        //TODO auch " kommen manchmal am Ende vor
        pos = name.lastIndexOf('"');
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        ENTITY entity = repo.findByName(name);
        if (entity == null) {
            entity = clazz.getDeclaredConstructor().newInstance();
            entity.setName(name);
            entity = repo.save(entity);
        }
        return entity;
    }


    public long getCount() {
        return count;
    }

    public long getFailed() {
        return failed;
    }

    public long getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }
}
