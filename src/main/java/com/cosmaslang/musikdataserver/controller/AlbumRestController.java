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
        if (album != null) {
            return albumRepository.findByNameContainingIgnoreCase(album).stream().sorted().toList();
        } else if (komponist != null) {
            return albumRepository.findByKomponist(komponist).stream().sorted().toList();
        } else if (werk != null) {
            return albumRepository.findByWerkLike(werk).stream().sorted().toList();
        } else if (genre != null) {
            //"von Hand" wäre:
            //List<Track> tracks = trackRepository.findByGenreLike(genre);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            //über spezielle Query:
            return albumRepository.findByGenreLike(genre).stream().sorted().toList();
        } else if (interpret != null) {
            //List<Track> tracks = trackRepository.findByInterpretenLike(interpret);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            return albumRepository.findByInterpretLike(interpret).stream().sorted().toList();
        } else if (id != null) {
            return getEntitiesIfExists(id, albumRepository);
        }
        return getAll(albumRepository);
    }

    @Override
    public Album findById(@PathVariable Long id) {
        return getEntityIfExists(id, albumRepository);
    }

    @Override
    public String remove(@RequestParam() Long id) {
        //zugehörige Tracks entfernen erledigt Cascadierung
        Optional<Album> album = albumRepository.findById(id);
        if (album.isPresent()) {
            albumRepository.delete(album.get());
            return album + " removed";
        }
        return "album " + id + " not found!";
    }
}
