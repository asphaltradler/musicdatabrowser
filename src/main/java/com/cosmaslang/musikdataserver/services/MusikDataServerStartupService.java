package com.cosmaslang.musikdataserver.services;

import com.cosmaslang.musikdataserver.db.entities.*;
import com.cosmaslang.musikdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikdataserver.db.repositories.TrackRepository;

import java.io.IOException;

public interface MusikDataServerStartupService {

    void setMediaDirectories(String rootDir, String startDir) throws IOException;

    void init();

    void start();

    TrackRepository getTrackRepository();
    NamedEntityRepository<Interpret> getInterpretRepository();
    NamedEntityRepository<Album> getAlbumRepository();
    NamedEntityRepository<Werk> getWerkRepository();
    NamedEntityRepository<Genre> getGenreRepository();
    NamedEntityRepository<Komponist> getKomponistRepository();
}
