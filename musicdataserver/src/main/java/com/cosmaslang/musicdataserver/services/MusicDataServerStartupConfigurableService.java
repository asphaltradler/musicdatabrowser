package com.cosmaslang.musicdataserver.services;

import com.cosmaslang.musicdataserver.MusicFileScanner;
import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.logging.Logger;

@Service
public class MusicDataServerStartupConfigurableService implements MusicDataServerStartupService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    MusicDataServerConfiguration musicDataServerConfiguration;
    @Autowired
    ApplicationContext context;
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

    @Override
    public void configure() throws IOException {
        logger.config("configure");
        Path rootDirPath = musicDataServerConfiguration.getRootPath();
        if (!rootDirPath.toFile().exists()) {
            throw new FileNotFoundException(MessageFormat.format("Root directory {0} doesn't exist", rootDirPath));
        }
        Path startDirPath = musicDataServerConfiguration.getStartPath();
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
        MusicFileScanner scanner = context.getBean(MusicFileScanner.class);
        scanner.start();
    }

    @Override
    public void start() {
        logger.info(String.format("MusicRepository starting from %s", musicDataServerConfiguration.getRootPath()));
        logger.info(getInfo());
    }

    @Override
    public String getInfo() {
        long count = trackRepository.count();
        if (count > 0) {
            return String.format("MusicRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Werke, %d Genres, %d Interpreten, %d Dokumente\n",
                    count, albumRepository.count(), composerRepository.count(), workRepository.count(), genreRepository.count(), artistRepository.count(), documentRepository.count());
        } else {
            return "Service gestartet aber noch leer.";
        }
    }
}
