package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.NamedEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Gemeinsames Interface für Repositories zu Datenbank-Tables,
 * die (nur) eine Column name haben.
 *
 * @param <ENTITY> eine NamedEntity
 */
@NoRepositoryBean
public interface NamedEntityRepository<ENTITY extends NamedEntity> extends CrudRepository<ENTITY, Long> {
    ENTITY findByName(String name);

    List<ENTITY> findByNameContainingIgnoreCase(String name);
}
