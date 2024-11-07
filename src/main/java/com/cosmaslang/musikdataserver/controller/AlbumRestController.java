package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Album;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/musik/album")
public class AlbumRestController extends AbstractMusikRestController<Album> {
    @Override
    public Stream<Album> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.logCall(track, album, komponist, werk, genre, interpret);
        if (album != null) {
            return albumRepository.streamByNameContainsIgnoreCaseOrderByName(album);
        } else if (komponist != null) {
            return albumRepository.streamDistinctByTracks_Komponist_NameOrderByName(komponist);
        } else if (werk != null) {
            return albumRepository.streamDistinctByTracks_Werk_NameContainsIgnoreCaseOrderByName(werk);
        } else if (genre != null) {
            //"von Hand" wäre:
            //Stream<Track> tracks = trackRepository.streamByGenreLike(genre);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            //über spezielle Query:
            return albumRepository.streamDistinctByTracks_Genres_NameContainsIgnoreCaseOrderByName(genre);
        } else if (interpret != null) {
            //Stream<Track> tracks = trackRepository.streamByInterpretenLike(interpret);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            return albumRepository.streamDistinctByTracks_Interpreten_NameContainsIgnoreCaseOrderByName(interpret);
        }
        return getAll(albumRepository);
    }

    @Override
    public Stream<Album> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getEntitiesIfExists(albumId, albumRepository);
        } else if (komponistId != null) {
            return albumRepository.streamDistinctByTracks_Komponist_IdOrderByName(komponistId);
        } else if (werkId != null) {
            return albumRepository.streamDistinctByTracks_Werk_IdOrderByName(werkId);
        } else if (genreId != null) {
            return albumRepository.streamDistinctByTracks_Genres_IdOrderByName(genreId);
        } else if (interpretId != null) {
            return albumRepository.streamDistinctByTracks_Interpreten_IdOrderByName(interpretId);
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
