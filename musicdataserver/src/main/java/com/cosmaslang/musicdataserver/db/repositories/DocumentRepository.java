package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Document;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends NamedEntityRepository<Document> {
    @Override
    default String getName() { return "Document"; }
    Optional<Document> findByHash(String hash);
}
