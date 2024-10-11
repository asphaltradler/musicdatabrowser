package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Interpret;
import org.springframework.stereotype.Repository;

@Repository
public interface InterpretRepository extends NamedEntityRepository<Interpret> {
}
