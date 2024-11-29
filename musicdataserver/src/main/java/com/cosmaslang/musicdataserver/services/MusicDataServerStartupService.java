package com.cosmaslang.musicdataserver.services;

import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;

import java.io.IOException;

public interface MusicDataServerStartupService {

    void setMediaDirectories(String rootDir, String startDir) throws IOException;

    void init();

    void start();

    TrackRepository getTrackRepository();
    NamedEntityRepository<Artist> getartistRepository();
    NamedEntityRepository<Album> getAlbumRepository();
    NamedEntityRepository<Work> getworkRepository();
    NamedEntityRepository<Genre> getGenreRepository();
    NamedEntityRepository<Composer> getcomposerRepository();
}
