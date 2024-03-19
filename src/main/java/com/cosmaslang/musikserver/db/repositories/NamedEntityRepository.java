package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.NamedEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Gemeinsames Interface für Repositories zu Datenbank-Tables,
 * die (nur) eine Column name haben.
 * @param <T> eine NamedEntity
 */
@NoRepositoryBean
public interface NamedEntityRepository<T extends NamedEntity> extends CrudRepository<T, Long> {
    T findByName(String name);
    List<T> findByNameContaining(String name);
 }
