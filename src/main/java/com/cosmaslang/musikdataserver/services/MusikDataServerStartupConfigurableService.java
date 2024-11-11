package com.cosmaslang.musikdataserver.services;

import com.cosmaslang.musikdataserver.MusikScanner;
import com.cosmaslang.musikdataserver.configuration.MusikDataServerConfiguration;
import com.cosmaslang.musikdataserver.db.entities.*;
import com.cosmaslang.musikdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikdataserver.db.repositories.TrackRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
@Qualifier("musikdataserverStartup")
public class MusikDataServerStartupConfigurableService implements MusikDataServerStartupService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @PersistenceContext
    private EntityManager entityManager;

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

    private Path rootDirPath;

    /**
     * Wird von der {@link MusikDataServerConfiguration} gesetzt
     */
    @Override
    public void setRootDir(String filename) {
        logger.info(MessageFormat.format("Setting root directory={0}", filename));
        rootDirPath = new File(filename).toPath();
    }

    @Override
    public void init() {
        logger.info("init");
        try {
            scanMusikdirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanMusikdirectory() throws IOException {
        MusikScanner scanner = new MusikScanner(this);
        scanner.scan(rootDirPath);
    }

    @Override
    public void start() {
        logger.info(String.format("starting from dir %s", this.rootDirPath));
        logger.info(String.format("MusikRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Werke, %d Genres, %d Interpreten\n",
                trackRepository.count(), albumRepository.count(), komponistRepository.count(), werkRepository.count(), genreRepository.count(), interpretRepository.count()));
        //listAllTracks();
        //findAlbumWithInterpret("John");
    }

    @Override
    public TrackRepository getTrackRepository() {
        return trackRepository;
    }

    @Override
    public NamedEntityRepository<Interpret> getInterpretRepository() {
        return interpretRepository;
    }

    @Override
    public NamedEntityRepository<Album> getAlbumRepository() {
        return albumRepository;
    }

    @Override
    public NamedEntityRepository<Werk> getWerkRepository() {
        return werkRepository;
    }

    @Override
    public NamedEntityRepository<Genre> getGenreRepository() {
        return genreRepository;
    }

    @Override
    public NamedEntityRepository<Komponist> getKomponistRepository() {
        return komponistRepository;
    }

    public void findAlbumWithInterpret(String name) {
        logger.info(String.format("Albums with interpret %s", name));
        Stream<?> albums = entityManager.createQuery(
                        "select a from Album a join Track t on t.album = a where t in (select i.tracks from Interpret i where i.name ilike '%'||:name||'%')")
                .setParameter("name", name)
                .getResultStream();
        albums.forEach(a -> logger.info(a.toString()));
    }

}
