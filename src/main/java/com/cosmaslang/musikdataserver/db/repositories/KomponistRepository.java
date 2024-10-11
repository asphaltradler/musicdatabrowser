package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Komponist;
import org.springframework.stereotype.Repository;

@Repository
public interface KomponistRepository extends NamedEntityRepository<Komponist> {
}
