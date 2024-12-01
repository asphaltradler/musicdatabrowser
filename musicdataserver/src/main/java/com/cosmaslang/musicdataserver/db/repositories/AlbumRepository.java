package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Album;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends TrackDependentRepository<Album> {
}
