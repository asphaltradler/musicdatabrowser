package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Album;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/musik/album")
public class AlbumRestController extends AbstractMusikRestController<Album> {
    @Override
    public List<Album> get(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        List<Album> alben;
        if (album != null) {
            alben = albumRepository.findByNameContaining(album);
        } else if (komponist != null) {
            alben = albumRepository.findByKomponist(komponist);
        } else if (werk != null) {
            alben = albumRepository.findByWerkLike(werk);
        } else if (genre != null) {
            alben = albumRepository.findByGenreLike(genre);
        } else if (interpret != null) {
            //List<Track> tracks = trackRepository.findByInterpretenLike(interpret);
            //alben = tracks.stream().map(Track::getAlbum).distinct().toList();
            alben = albumRepository.findByInterpretLike(interpret);
        } else if (id != null) {
            alben = getEntitiesIfExists(id, albumRepository);
        } else {
            alben = getAll(albumRepository);
        }
        return alben;
    }

    @Override
    public Album findById(@PathVariable Long id) {
        return getEntityIfExists(id, albumRepository);
    }

    @Override
    public String remove(@RequestParam() Long id) {
        //TODO zugehörige Tracks entfernen
        Optional<Album> album = albumRepository.findById(id);
        if (album.isPresent()) {
            albumRepository.delete(album.get());
            return album + " removed";
        }
        return "album " + id + " not found!";
    }
}
