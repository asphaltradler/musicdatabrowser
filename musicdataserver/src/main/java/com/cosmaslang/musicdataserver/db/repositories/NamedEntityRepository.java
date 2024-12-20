package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.NamedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Gemeinsames Interface für Repositories zu Datenbank-Tables,
 * die mindestens eine Column name haben.
 *
 * @param <ENTITY> eine NamedEntity
 */
@NoRepositoryBean
public interface NamedEntityRepository<ENTITY extends NamedEntity> extends JpaRepository<ENTITY, Long> {
    String getName();

    ENTITY findByName(String name);
    Page<ENTITY> findByNameContainsIgnoreCaseOrderByName(String name, Pageable pageable);
    Page<ENTITY> findById(long id, Pageable pageable);
}

