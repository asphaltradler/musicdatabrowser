package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Work;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends TrackDependentRepository<Work> {
    @Override
    default String getName() { return "Work"; }
}
