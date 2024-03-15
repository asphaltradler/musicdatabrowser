package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Album;
import com.cosmaslang.musikserver.db.entities.Komponist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cosmaslang.musikserver.db.entities.Werk;

@Repository
public interface WerkRepository extends NamedRepository<Werk> {
}
