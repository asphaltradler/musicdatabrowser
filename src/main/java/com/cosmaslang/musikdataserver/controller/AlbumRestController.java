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
    public List<Album> find(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        super.find(track, album, komponist, werk, genre, interpret, id);
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
        }
        return get(id, albumRepository);
    }

    @Override
    public List<Album> get(@RequestParam(required = false) Long trackId,
                              @RequestParam(required = false) Long albumId,
                              @RequestParam(required = false) Long komponistId,
                              @RequestParam(required = false) Long werkId,
                              @RequestParam(required = false) Long genreId,
                              @RequestParam(required = false) Long interpretId) {
        super.get(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getEntitiesIfExists(albumId, albumRepository);
        } else if (komponistId != null) {
            return albumRepository.findByKomponistId(komponistId);
        } else if (werkId != null) {
            return albumRepository.findByWerkId(werkId);
        } else if (genreId != null) {
            return albumRepository.findByGenreId(genreId);
        } else if (interpretId != null) {
            return albumRepository.findByInterpretId(interpretId);
        }

        return getAll(albumRepository);
    }

    @Override
    public Album getById(@PathVariable Long id) {
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
