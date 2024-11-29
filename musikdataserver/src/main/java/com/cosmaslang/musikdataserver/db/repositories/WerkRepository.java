package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Werk;
import org.springframework.stereotype.Repository;

@Repository
public interface WerkRepository extends NamedEntityRepository<Werk> {
}
