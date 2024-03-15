package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Genre;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends NamedEntityRepository<Genre> {
}
