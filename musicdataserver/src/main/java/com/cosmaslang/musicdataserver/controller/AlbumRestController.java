package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Album;
import com.cosmaslang.musicdataserver.db.entities.Document;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/music/album")
public class AlbumRestController extends TrackDependentRestController<Album> {
    @Override
    protected TrackDependentRepository<Album> getMyRepository() {
        return albumRepository;
    }


    @GetMapping("/id/{id}/albumart")
    public ResponseEntity<?> getAlbumArt(@PathVariable Long id) {
        Document doc = trackRepository.findFirstByAlbumId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getAlbumart();
        return getResponseEntity(doc);
    }

    @GetMapping("/id/{id}/booklet")
    public ResponseEntity<?> getBooklet(@PathVariable Long id) {
        Document doc = trackRepository.findFirstByAlbumId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getBooklet();
        return getResponseEntity(doc);
    }
}