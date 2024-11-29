package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.NamedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.stream.Stream;

/**
 * Gemeinsames Interface für Repositories zu Datenbank-Tables,
 * die mindestens eine Column name haben.
 *
 * @param <ENTITY> eine NamedEntity
 */
@NoRepositoryBean
public interface NamedEntityRepository<ENTITY extends NamedEntity> extends JpaRepository<ENTITY, Long> {
    ENTITY findByName(String name);

    List<ENTITY> findByNameContainsIgnoreCaseOrderByName(String name);
}
