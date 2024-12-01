package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Composer;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/composer")
public class ComposerRestController extends TrackDependentRestController<Composer> {
    @Override
    protected TrackDependentRepository<Composer> getMyRepository() {
        return composerRepository;
    }
}
