package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Composer;
import org.springframework.stereotype.Repository;

@Repository
public interface ComposerRepository extends TrackDependentRepository<Composer> {
    @Override
    default String getName() { return "Composer"; }
}
