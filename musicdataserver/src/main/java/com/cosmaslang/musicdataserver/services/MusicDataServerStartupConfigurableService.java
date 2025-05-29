package com.cosmaslang.musicdataserver.services;

import com.cosmaslang.musicdataserver.MusicFileScanner;
import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    TrackDependentRepository<Album> albumRepository;
    @Autowired
    TrackDependentRepository<Composer> composerRepository;
    @Autowired
    TrackDependentRepository<Work> workRepository;
    @Autowired
    TrackDependentRepository<Genre> genreRepository;
    @Autowired
    TrackDependentRepository<Artist> artistRepository;
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

    @Override
    @Transactional
    public void deleteOrphans() {
        logger.info("Orphaned entities:");
        List<TrackDependentRepository<?>> repos = Arrays.asList(
                albumRepository, workRepository, genreRepository, artistRepository);
        repos.forEach(this::deleteOrphanedEntities);
    }

    private <E extends NamedEntity> void deleteOrphanedEntities(TrackDependentRepository<E> repo) {
        Set<E> orphans = repo.findByTracksIsEmpty();
        if (!orphans.isEmpty()) {
            logger.info(MessageFormat.format("  {0}: {1}", repo.getName(), orphans.stream().map(e -> '\'' + e.getName() + '\'').collect(Collectors.joining(", "))));
            repo.deleteAllByTracksIsEmpty();
        }
    }
}
