package com.cosmaslang.musicdataserver.services;

import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;

import java.io.IOException;

public interface MusicDataServerStartupService {

    void configure() throws IOException;
    void init();
    void start();

    TrackRepository getTrackRepository();
    NamedEntityRepository<Artist> getArtistRepository();
    NamedEntityRepository<Album> getAlbumRepository();
    NamedEntityRepository<Work> getWorkRepository();
    NamedEntityRepository<Genre> getGenreRepository();
    NamedEntityRepository<Composer> getComposerRepository();
}
