package com.cosmaslang.musicdataserver;

import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
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
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
public class MusicFileScanner {
    @Autowired
    TrackRepository trackRepository;
    @Autowired
    NamedEntityRepository<Artist> artistRepository;
    @Autowired
    NamedEntityRepository<Album> albumRepository;
    @Autowired
    NamedEntityRepository<Work> workRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;
    @Autowired
    NamedEntityRepository<Composer> composerRepository;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    MusicDataServerConfiguration musicDataServerConfiguration;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final static List<String> audioFileExtensions = Stream.of(SupportedFileFormat.values()).map(SupportedFileFormat::getFilesuffix).toList();

    private final static List<String> imageNames = Arrays.asList("folder", "cover");
    private final static List<String> imageExts = Arrays.asList("jpg", "png", "gif");
    private final static List<String> bookletExts = Arrays.asList("pdf", "pub");

    private long count = 0;
    private long created = 0;
    private long reused = 0;
    private long unchanged = 0;
    private long updated = 0;
    private long failed = 0;

    public void start() throws IOException {
        logger.info(MessageFormat.format("Scanning {0} from start {1}",
                musicDataServerConfiguration.getRootPath(), musicDataServerConfiguration.getStartPath()));

        long startTime = System.currentTimeMillis();
        scanDirectory(musicDataServerConfiguration.getStartPath().toFile());
        long endTime = System.currentTimeMillis() - startTime;

        logger.info(String.format("Found %d tracks in %ds", count, endTime / 1000));
        logger.info(String.format("Created/updated/unchanged/re-used/failed tracks: %d/%d/%d/%d/%d",
                created, updated, unchanged, reused, failed));
    }

    private void scanDirectory(final File dir) throws IOException {
        if (dir.isDirectory()) {
            try (Stream<Path> paths = Files.list(dir.toPath())) {
                //erst alle Unterverzeichnisse nach unten steigen
                paths.map(Path::toFile).filter(File::isDirectory).forEach(
                        f -> {
                            try {
                                scanDirectory(f);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }

            final Document albumArt = getDocument(getDocumentFile(dir, imageExts, imageNames));
            final Document booklet = getDocument(getDocumentFile(dir, bookletExts, null));

            try (Stream<Path> paths = Files.list(dir.toPath())) {
                Stream<Track> tracks = paths.map(Path::toFile).filter(
                                f -> audioFileExtensions.contains(Utils.getExtension(f).toLowerCase()))
                        .map(this::processAudioFile).filter(Objects::nonNull);

                tracks.forEach(track -> {
                    //könnte schon eins embedded gesetzt sein
                    if (track.getAlbumart() == null) {
                        track.setAlbumart(albumArt);
                    }
                    track.setBooklet(booklet);
                    trackRepository.save(track);
                });
            }
        }
    }

    private Document getDocument(File file) {
        Document document = null;
        if (file != null && file.exists()) {
            String relativePathString = musicDataServerConfiguration.getRelativePath(file.toPath());
            document = new Document(relativePathString);
            Optional<Document> optional = documentRepository.findOne(Example.of(document));
            if (optional.isPresent()) {
                document = optional.get();
            } else {
                document = documentRepository.save(document);
            }
        }
        return document;
    }

    @Nullable
    private static File getDocumentFile(File dir, List<String> extensions, List<String> names) throws IOException {
        try (Stream<Path> paths = Files.list(dir.toPath())) {
            return paths.map(Path::toFile).filter(
                    f -> extensions.contains(Utils.getExtension(f)))
                    .filter(f -> MusicFileScanner.filenameContainedIn(f.getName(), names))
                    .findFirst().orElse(null);
        }
    }

    private static boolean filenameContainedIn(String filename, List<String> names) {
        if (names== null || names.isEmpty()) {
            return true;
        } else {
            int idx = filename.lastIndexOf('.');
            if (idx == -1) {
                idx = filename.length();
            }
            String name = filename.substring(0, idx);
            return names.contains(name);
        }
    }
    @Transactional
    @Nullable
    protected Track processAudioFile(File file) {
        count++;
        Track track = null;
        try {
            track = createOrUpdateTrack(file);
            //null markiert unveränderte Tracks, die nicht mehr gespeichert werden müssen
            //TODO jetzt nicht mehr möglich, da Image und Booklet noch gesetzt werden müssen
        } catch (Throwable t) {
            failed++;
            logger.log(Level.WARNING, "Unable to read file: " + file, t);
        }
        logger.info("processed " + file.getName());
        return track;
    }

    private Track createOrUpdateTrack(File file) {
        //path unabhängig von Filesystem notieren, ausgehend von rootDir
        //String pathString = filepath.subpath(rootPath.getNameCount(), filepath.getNameCount()).toString().replace('\\', '/');
        String pathString =  musicDataServerConfiguration.getRelativePath(file.toPath());
        Track track = trackRepository.findByPath(pathString);
        if (track == null) {
            track = new Track();
            //default
            track.setName(file.getName());
            logger.info("create new track " + pathString);
            created++;
        } else if (isFileNotModifiedAfterTrack(file, track)) {
            logger.info("-   skipping unchanged track " + pathString);
            unchanged++;
            //muss nicht nochmal gespeichert werden
            //TODO leider können wir das wg. Image und Booklet nicht mehr mit Sicherheit sagen
            //return null
            return track;
        }
        else {
            logger.info("-   update track " + pathString);
            updated++;
        }

        Tag tag;
        AudioHeader header = null;
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            tag = audioFile.getTag();
            //technical data
            header = audioFile.getAudioHeader();
            if (header != null) {
                String hash = getHash(header, file.getName());
                Track existingTrack = trackRepository.findByHash(hash);
                if (existingTrack != null) {
                    //wir übernehmen die Daten aus dem bisherigen Track
                    track = existingTrack;
                    reused++;
                    if (isFileNotModifiedAfterTrack(file, track)) {
                        //nur neue Position, aber keine Änderung an Daten
                        logger.info(MessageFormat.format("-   skipping re-used unchanged track {0} from path {1}", pathString, track.getPath()));
                        unchanged++;
                        //path muss aber neu gespeichert werden
                        track.setPath(pathString);
                        return track;
                    }
                    logger.info(MessageFormat.format("-   re-using track {0} from path {1}", pathString, track.getPath()));
                } else {
                    track.setHash(hash);
                }
                track.setBitsPerSample(header.getBitsPerSample());
                track.setBitrate(header.getBitRateAsNumber());
                track.setSamplerate(header.getSampleRateAsNumber());
                track.setEncoding(header.getFormat());
                track.setLengthInSeconds(header.getTrackLength());
                //ist null bei Ogg, WMA, manchen WAV, etc.
                track.setNoOfSamples(getNumberOfSamples(header));
            }
        } catch (PersistenceException e) {
            throw e;
        } catch (Throwable t) {
            logger.log(Level.WARNING, MessageFormat.format("error when processing track {0} with header {1}", pathString, header == null ? "NULL" : header),
                    t);
            failed++;
            return null;
        }

        //Daten direkt aus File
        track.setPath(pathString);
        track.setSize(file.length());
        track.setFileModifiedDate(new Date(file.lastModified()));

        if (tag != null) {
            try {
                String str = tag.getFirst(FieldKey.TITLE);
                if (StringUtils.isNotBlank(str)) {
                    track.setName(str);
                }
                str = tag.getFirst(FieldKey.TRACK);
                if (StringUtils.isNotBlank(str)) {
                    try {
                        track.setTracknumber(Integer.valueOf(str));
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING, "invalid track number " + str + " for track " + pathString);
                    }
                }
                str = tag.getFirst(FieldKey.ALBUM);
                if (StringUtils.isNotBlank(str)) {
                    Album album = createOrUpdateEntity(Album.class, albumRepository, str);
                    track.setAlbum(album);
                    //album.getTracks().add(track);
                }
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    Document document = new Document(artwork.getBinaryData(), artwork.getMimeType());
                    if (StringUtils.isNotBlank(artwork.getDescription())) {
                        document.setName(artwork.getDescription());
                    }
                    documentRepository.save(document);
                    track.setAlbumart(document);
                }
                str = tag.getFirst(FieldKey.COMPOSER);
                if (StringUtils.isNotBlank(str)) {
                    Composer composer = createOrUpdateEntity(Composer.class, composerRepository, str);
                    track.setComposer(composer);
                }
                str = tag.getFirst(Track.FIELDKEY_WORK);
                if (StringUtils.isNotBlank(str)) {
                    Work work = createOrUpdateEntity(Work.class, workRepository, str);
                    track.setWork(work);
                }
                //ManyToMany Zuordnung
                List<TagField> tagFields = tag.getFields(FieldKey.ARTIST);
                if (tagFields != null) {
                    //alle als Liste setzen → dann ist kein teilweises update eines vorhandenen tracks möglich
                    Set<Artist> artists = new HashSet<>();
                    for (TagField field : tagFields) {
                        str = field.toString();
                        if (StringUtils.isNotBlank(str)) {
                            Artist artist = createOrUpdateEntity(Artist.class, artistRepository, str);
                            //track.addartist(artist);
                            artists.add(artist);
                        }
                    }
                    track.setArtists(artists);
                }
                //ManyToMany Zuordnung
                tagFields = tag.getFields(FieldKey.GENRE);
                if (tagFields != null) {
                    Set<Genre> genres = new HashSet<>();
                    for (TagField field : tagFields) {
                        str = field.toString();
                        if (StringUtils.isNotBlank(str)) {
                            Genre genre = createOrUpdateEntity(Genre.class, genreRepository, str);
                            //track.addGenre(genre);
                            genres.add(genre);
                        }
                    }
                    track.setGenres(genres);
                }

                //TODO comment länger als 255 möglich?
                String comment = tag.getFirst(FieldKey.COMMENT);
                //abschneiden
                track.setComment(comment.substring(0, Math.min(255, comment.length())));
                track.setPublisher(tag.getFirst(Track.FIELDKEY_ORGANIZATION));
                track.setPublishedDate(tag.getFirst(FieldKey.YEAR));
            } catch (PersistenceException e) {
                throw e;
            } catch (Throwable t) {
                logger.log(Level.WARNING, "error when processing track " + pathString
                        + " with tag " + tag, t);
                failed++;
                return null;
            }
        }

        return track;
    }

    private static boolean isFileNotModifiedAfterTrack(File file, Track track) {
        Date modified = track.getFileModifiedDate();
        return modified != null && modified.getTime() >= file.lastModified();
    }

    private static String getHash(AudioHeader header, String filename) {
        //nur Flac hat (meistens) einen korrekten Audio-MD5, der unabhängig von Tags ist
        if (header instanceof FlacAudioHeader) {
            String hash = ((FlacAudioHeader) header).getMd5();
            //fehlerhafte Flacs haben 0-MD5
            if (!containsOnlyZero(hash)) {
                return hash;
            }
        }
        //beim Rest muss filepath-hash plus Länge in samples reichen, um Änderungen anzuzeigen
        return getFileHash(filename, header);
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

    private static String getFileHash(String filename, AudioHeader header) {
        return Long.toHexString(filename.hashCode())
                + '-' + Long.toHexString(getNumberOfSamples(header));
    }

    private static Long getNumberOfSamples(AudioHeader header) {
        Long noOfSamples = header.getNoOfSamples();
        if (noOfSamples == null) {
            //dann selbst berechnen (wieso macht das jaudiotagger nicht schon?)
            noOfSamples = ((long) header.getTrackLength() * header.getSampleRateAsNumber());
        }
        return noOfSamples;
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
}
