package com.cosmaslang.musikserver.db.repositories;

import org.springframework.stereotype.Repository;

import com.cosmaslang.musikserver.db.entities.Album;

@Repository
public interface AlbumRepository extends NamedRepository<Album> {
}
