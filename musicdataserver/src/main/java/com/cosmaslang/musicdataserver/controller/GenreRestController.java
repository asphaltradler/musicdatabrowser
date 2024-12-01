package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Genre;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/genre")
public class GenreRestController extends TrackDependentRestController<Genre> {
    @Override
    protected TrackDependentRepository<Genre> getMyRepository() {
        return genreRepository;
    }
}
