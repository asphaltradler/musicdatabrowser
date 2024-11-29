package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Komponist;
import org.springframework.stereotype.Repository;

@Repository
public interface KomponistRepository extends NamedEntityRepository<Komponist> {
}
