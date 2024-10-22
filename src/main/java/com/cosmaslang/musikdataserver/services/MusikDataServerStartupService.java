package com.cosmaslang.musikdataserver.services;

import com.cosmaslang.musikdataserver.db.entities.*;
import com.cosmaslang.musikdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikdataserver.db.repositories.TrackRepository;

public interface MusikDataServerStartupService {

    void setRootDir(String rootDir);

    void init();

    void start();

    TrackRepository getTrackRepository();
    NamedEntityRepository<Interpret> getInterpretRepository();
    NamedEntityRepository<Album> getAlbumRepository();
    NamedEntityRepository<Werk> getWerkRepository();
    NamedEntityRepository<Genre> getGenreRepository();
    NamedEntityRepository<Komponist> getKomponistRepository();
}
