package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Album;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends NamedRepository<Album> {
}
