package com.cosmaslang.musicdataserver.services;

import com.cosmaslang.musicdataserver.MusicFileScanner;
import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
@Qualifier("musicdataserverStartup")
public class MusicDataServerStartupConfigurableService implements MusicDataServerStartupService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ConfigurableApplicationContext context;
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

    private Path rootDirPath;
    private Path startDirPath;

    /**
     * Wird von der {@link MusicDataServerConfiguration} gesetzt
     */
    @Override
    public void setMediaDirectories(String rootdir, String startdir) throws IOException {
        logger.info(MessageFormat.format("Setting root directory={0}, start directory={1}", rootdir, startdir));
        rootDirPath = new File(rootdir).toPath();
        startDirPath = new File(startdir).toPath();
        if (!startDirPath.toFile().exists()) {
            throw new FileNotFoundException(MessageFormat.format("Start directory {0} doesn't exist", startDirPath));
        }
        if (!startDirPath.startsWith(rootDirPath)) {
            throw new IOException(MessageFormat.format("Start directory {0} is not in {1}", startDirPath, rootDirPath));
        }
    }

    @Override
    public void init() {
        logger.info("init");
        try {
            scanMusicdirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanMusicdirectory() throws IOException {
        MusicFileScanner scanner = new MusicFileScanner(this);
        scanner.scan(rootDirPath, startDirPath);
    }

    @Override
    public void start() {
        logger.info(String.format("starting from dir %s", this.rootDirPath));
        logger.info(String.format("MusicRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Werke, %d Genres, %d Interpreten\n",
                trackRepository.count(), albumRepository.count(), composerRepository.count(), workRepository.count(), genreRepository.count(), artistRepository.count()));
        //listAllTracks();
        //findAlbumWithartist("John");
    }

    @Override
    public TrackRepository getTrackRepository() {
        return trackRepository;
    }

    @Override
    public NamedEntityRepository<Artist> getArtistRepository() {
        return artistRepository;
    }

    @Override
    public NamedEntityRepository<Album> getAlbumRepository() {
        return albumRepository;
    }

    @Override
    public NamedEntityRepository<Work> getWorkRepository() {
        return workRepository;
    }

    @Override
    public NamedEntityRepository<Genre> getGenreRepository() {
        return genreRepository;
    }

    @Override
    public NamedEntityRepository<Composer> getComposerRepository() {
        return composerRepository;
    }

    public void findAlbumWithartist(String name) {
        logger.info(String.format("Albums with artist %s", name));
        Stream<?> albums = entityManager.createQuery(
                        "select a from Album a join Track t on t.album = a where t in (select i.tracks from Artist i where i.name ilike '%'||:name||'%')")
                .setParameter("name", name)
                .getResultStream();
        albums.forEach(a -> logger.info(a.toString()));
    }

}
