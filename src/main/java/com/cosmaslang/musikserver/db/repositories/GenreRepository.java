package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Album;
import com.cosmaslang.musikserver.db.entities.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository  extends NamedRepository<Genre> {
}
