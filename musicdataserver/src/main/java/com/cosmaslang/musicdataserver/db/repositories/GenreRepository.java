package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Genre;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends TrackDependentRepository<Genre> {
    @Override
    default String getName() { return "Genre"; }
}
