package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Genre;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends NamedEntityRepository<Genre> {
}
