package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Album;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/album")
public class AlbumRestController extends TrackDependentRestController<Album> {
    @Override
    protected TrackDependentRepository<Album> getMyRepository() {
        return albumRepository;
    }
}