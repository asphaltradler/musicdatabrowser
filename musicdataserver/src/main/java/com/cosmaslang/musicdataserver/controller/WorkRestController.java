package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Work;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/work")
public class WorkRestController extends TrackDependentRestController<Work> {
    @Override
    protected TrackDependentRepository<Work> getMyRepository() {
        return workRepository;
    }
}
