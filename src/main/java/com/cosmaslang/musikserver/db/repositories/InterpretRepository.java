package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Album;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cosmaslang.musikserver.db.entities.Interpret;

@Repository
public interface InterpretRepository extends NamedRepository<Interpret> {
}
