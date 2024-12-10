package com.cosmaslang.musicdataserver.services;

import com.cosmaslang.musicdataserver.MusicFileScanner;
import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
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
import java.util.logging.Logger;

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
    @Autowired
    DocumentRepository fileContentRepository;
    @Autowired
    MusicDataServerConfiguration musicDataServerConfiguration;

    private Path rootDirPath;
    private Path startDirPath;

    @Override
    public void configure() throws IOException {
        logger.info("configure");
        rootDirPath = new File(musicDataServerConfiguration.getRootdir()).toPath();
        if (!rootDirPath.toFile().exists()) {
            throw new FileNotFoundException(MessageFormat.format("Root directory {0} doesn't exist", rootDirPath));
        }
        startDirPath = new File(musicDataServerConfiguration.getStartdir()).toPath();
        if (!startDirPath.toFile().exists()) {
            throw new FileNotFoundException(MessageFormat.format("Start directory {0} doesn't exist", startDirPath));
        }
        if (!startDirPath.startsWith(rootDirPath)) {
            throw new IOException(MessageFormat.format("Start directory {0} is not in root {1}", startDirPath, rootDirPath));
        }
    }

    @Override
    public void init() throws IOException {
        logger.info("init");
        MusicFileScanner scanner = new MusicFileScanner(this);
        scanner.scan(rootDirPath, startDirPath);
    }

    @Override
    public void start() {
        logger.info(String.format("MusicRepository starting from %s", this.rootDirPath));
        logger.info(String.format("MusicRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Werke, %d Genres, %d Interpreten\n",
                trackRepository.count(), albumRepository.count(), composerRepository.count(), workRepository.count(), genreRepository.count(), artistRepository.count()));
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
    @Override
    public DocumentRepository getDocumentRepository() {
        return fileContentRepository;
    }
}
