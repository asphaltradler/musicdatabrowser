package com.cosmaslang.musikserver.controller;

import com.cosmaslang.musikserver.db.entities.Album;
import com.cosmaslang.musikserver.db.entities.Track;
import com.cosmaslang.musikserver.db.repositories.AlbumRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/musik/album")
public class AlbumRestController extends AbstractMusikRestController<Album> {
    @Override
    public List<Album> get(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        List<Album> alben;
        if (album != null) {
            alben = Collections.singletonList(albumRepository.findByName(album));
        } else if (komponist != null) {
            alben = albumRepository.findByKomponist(komponist);
        } else if (werk != null) {
            alben = albumRepository.findByWerkLike(werk);
        } else if (genre != null) {
//            List<Genre> genres = genreRepository.findByNameContaining(genre);
//            List<Track> tracks = trackRepository.findByGenresIsIn(new HashSet<>(genres));
            List<Track> tracks = trackRepository.findByGenreLike(genre);
            alben = tracks.stream().map(Track::getAlbum).distinct().toList();
            //alben = albumRepository.findByGenreLike(genre);
        } else if (interpret != null) {
            //List<Interpret> interpreten = interpretRepository.findByNameContaining(interpret);
            //List<Track> tracks = trackRepository.findByInterpretenIsIn(new HashSet<>(interpreten));
            List<Track> tracks = trackRepository.findByInterpretenLike(interpret);
            alben = tracks.stream().map(Track::getAlbum).distinct().toList();
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
