package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Interpret;
import org.springframework.stereotype.Repository;

@Repository
public interface InterpretRepository extends NamedEntityRepository<Interpret> {
}
