package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Artist;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends TrackDependentRepository<Artist> {
    @Override
    default String getName() { return "Artist"; }
}
