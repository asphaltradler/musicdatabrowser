package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Artist;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/artist")
public class ArtistRestController extends TrackDependentRestController<Artist> {
    @Override
    protected TrackDependentRepository<Artist> getMyRepository() {
        return artistRepository;
    }
}
