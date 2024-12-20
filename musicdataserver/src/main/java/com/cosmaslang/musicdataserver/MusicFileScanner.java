package com.cosmaslang.musicdataserver;

import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    TrackDependentRepository<Artist> artistRepository;
    @Autowired
    TrackDependentRepository<Album> albumRepository;
    @Autowired
    TrackDependentRepository<Work> workRepository;
    @Autowired
    TrackDependentRepository<Genre> genreRepository;
    @Autowired
    TrackDependentRepository<Composer> composerRepository;
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

            final Document albumArt = getDocument(getDocumentFile(dir, imageExts, imageNames), true);
            final Document booklet = getDocument(getDocumentFile(dir, bookletExts, null), false);

            try (Stream<Path> paths = Files.list(dir.toPath())) {
                Stream<Track> tracks = paths.map(Path::toFile).filter(
                                f -> audioFileExtensions.contains(Utils.getExtension(f).toLowerCase()))
                        .map(this::processAudioFile).filter(Objects::nonNull);

                tracks.forEach(track -> {
                    //könnte schon eins embedded gesetzt sein
                    if (track.getAlbumart() == null) {
                        track.setAlbumart(albumArt);
                    }
                    if (track.getBooklet() == null) {
                        track.setBooklet(booklet);
                    }
                    try {
                        trackRepository.save(track);
                    } catch (RuntimeException e) {
                        logger.log(Level.WARNING, MessageFormat.format("error when processing track {0}", track.getPath()),
                                e);
                        failed++;
                    }
                });
            }
        }
    }

    private Document getDocument(File file, boolean withThumbnail) {
        Document document = null;
        if (file != null && file.exists()) {
            String relativePathString = musicDataServerConfiguration.getRelativePath(file.toPath());
            document = getOrCreateDocument(file, relativePathString, withThumbnail);
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
        if (names == null || names.isEmpty()) {
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
        logger.fine("processed " + file.getName());
        return track;
    }

    private Track createOrUpdateTrack(File file) {
        //path unabhängig von Filesystem notieren, ausgehend von rootDir
        //String pathString = filepath.subpath(rootPath.getNameCount(), filepath.getNameCount()).toString().replace('\\', '/');
        String pathString = musicDataServerConfiguration.getRelativePath(file.toPath());
        Track track = trackRepository.findByPath(pathString);
        if (track == null) {
            track = new Track();
            //default
            track.setName(file.getName());
            logger.fine("create new track " + pathString);
            created++;
        } else if (isFileNotModifiedAfterTrack(file, track)) {
            logger.finer("-   skipping unchanged track " + pathString);
            unchanged++;
            //muss nicht nochmal gespeichert werden
            //TODO leider können wir das wg. Image und Booklet nicht mehr mit Sicherheit sagen
            //return null
            return track;
        } else {
            logger.fine("-   update track " + pathString);
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
                String hash = getHash(header, file);
                Track existingTrack = trackRepository.findByHash(hash);
                if (existingTrack != null) {
                    //wir übernehmen die Daten aus dem bisherigen Track
                    track = existingTrack;
                    reused++;
                    if (isFileNotModifiedAfterTrack(file, track)) {
                        //nur neue Position, aber keine Änderung an Daten
                        logger.finer(MessageFormat.format("-   skipping re-used unchanged track {0} from path {1}", pathString, track.getPath()));
                        unchanged++;
                        //path muss aber neu gespeichert werden
                        track.setPath(pathString);
                        return track;
                    }
                    logger.finer(MessageFormat.format("-   re-using track {0} from path {1}", pathString, track.getPath()));
                } else {
                    track.setHash(hash);
                }
                track.setBitsPerSample(header.getBitsPerSample());
                track.setBitrate(header.getBitRateAsNumber());
                track.setSamplerate(header.getSampleRateAsNumber());
                track.setEncoding(header.getFormat());
                track.setLengthInSeconds(header.getTrackLength());
                //ist null bei Ogg, WMA, manchen WAV, etc.
                track.setNoOfSamples(getNoOfSamples(header));
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
                    Document document = getOrCreateDocument(artwork.getBinaryData(),
                            artwork.getMimeType(),
                            track.getName(), artwork.getDescription());
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

    private String getHash(AudioHeader header, File file) {
        //nur Flac hat (meistens) einen korrekten Audio-MD5, der unabhängig von Tags ist
        if (header instanceof FlacAudioHeader) {
            String hash = ((FlacAudioHeader) header).getMd5();
            //fehlerhafte Flacs haben 0-MD5
            if (!containsOnlyZero(hash)) {
                return hash;
            }
        }
        //beim Rest muss filepath-hash plus Länge in samples reichen, um Änderungen anzuzeigen
        return getFileHash(file, header);
    }

    private boolean containsOnlyZero(String hash) {
        if (hash != null) {
            for (int i = 0; i < hash.length(); i++) {
                if (hash.charAt(i) != '0') {
                    return false;
                }
            }
        }
        return true;
    }

    private String getFileHash(File file, AudioHeader header) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(file.getName().getBytes(StandardCharsets.UTF_8));
            md5.update(String.valueOf(file.length()).getBytes(StandardCharsets.UTF_8));
            md5.update(getNoOfSamples(header).toString().getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md5.digest();
            BigInteger bigInteger = new BigInteger(1, bytes);
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Encoding Fehler", e);
        }
        //fallback
        return Long.toHexString(Objects.hash(file.getName(), file.length(), getNoOfSamples(header)));
    }

    private static Long getNoOfSamples(AudioHeader header) {
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

    private @NotNull Document getOrCreateDocument(byte[] content, String mimeType, String trackName, String suffix) {
        Document document = new Document(content, mimeType, trackName, suffix);
        Optional<Document> existing = documentRepository.findByHash(document.getHash());
        if (existing.isPresent()) {
            Document existingDocument = existing.get();
            //nichts zu ändern
            if (existingDocument.getName().equals(document.getName())
                    && existing.get().getThumbnail() != null) {
                return existingDocument;
            }
            logger.finer(MessageFormat.format("-   updating document {0} => {1}", existingDocument, document));
            document = existingDocument;
            document.setTrackNameForEmbedded(trackName, suffix);
        } //ansonsten erzeugen wir document neu
        if (document.getThumbnail() == null) {
            try {
                document.setThumbnail(ImageUtilities.buildThumbnail(document.getEmbeddedDocument()));
            } catch (Throwable e) {
                logger.log(Level.WARNING, MessageFormat.format("Thumbnail creation for {0} not possible", document.getName()), e);
            }
        }
        return documentRepository.save(document);
    }

    private @NotNull Document getOrCreateDocument(File file, String relativePathString, boolean withThumbnail) {
        Document document = new Document(file, relativePathString);
        Optional<Document> existing = documentRepository.findByHash(document.getHash());
        if (existing.isPresent()) {
            Document existingDocument = existing.get();
            //nichts zu ändern
            if (existingDocument.getExternalDocument().equals(document.getExternalDocument())
                    && !withThumbnail || existingDocument.getThumbnail() != null) {
                return existingDocument;
            }
            logger.finer(MessageFormat.format("-   updating document {0} => {1}", existingDocument, document));
            document = existingDocument;
            //update: Pfad könnte sich geändert haben und hash trotzdem gleich geblieben
            //dann ändert sich das hash aber jetzt nach Neusetzen des Files
            document.setExternalDocument(file, relativePathString);
        }
        if (withThumbnail && document.getThumbnail() == null) {
            File originalFile = musicDataServerConfiguration.getFileFromRelativePath(
                    Path.of(document.getExternalDocument()));
            try {
                document.setThumbnail(ImageUtilities.buildThumbnail(originalFile));
                //kann auch null sein, falls Bild schon klein genug ist, dann muss auch kein Thumbnail gespeichert werden
            } catch (Throwable e) {
                logger.log(Level.WARNING, MessageFormat.format("Thumbnail creation for {0} not possible", document.getName()), e);
            }
        }
        return documentRepository.save(document);
    }
}
