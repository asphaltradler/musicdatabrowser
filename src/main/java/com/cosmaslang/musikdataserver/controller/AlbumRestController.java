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
        } else if (track != null) {
            return albumRepository.streamDistinctByTracksNameContainsIgnoreCase(track);
        } else if (komponist != null) {
            return albumRepository.streamDistinctByTracksKomponistNameOrderByName(komponist);
        } else if (werk != null) {
            return albumRepository.streamDistinctByTracksWerkNameContainsIgnoreCaseOrderByName(werk);
        } else if (genre != null) {
            //"von Hand" wäre:
            //Stream<Track> tracks = trackRepository.streamByGenreLike(genre);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            //über spezielle Query:
            return albumRepository.streamDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(genre);
        } else if (interpret != null) {
            //Stream<Track> tracks = trackRepository.streamByInterpretenLike(interpret);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            return albumRepository.streamDistinctByTracksInterpretenNameContainsIgnoreCaseOrderByName(interpret);
        }
        return getAll(albumRepository);
    }

    @Override
    public Stream<Album> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getEntitiesIfExists(albumId, albumRepository);
        } else if (trackId != null) {
            return albumRepository.streamByTracksId(trackId);
        } else if (komponistId != null) {
            return albumRepository.streamDistinctByTracksKomponistIdOrderByName(komponistId);
        } else if (werkId != null) {
            return albumRepository.streamDistinctByTracksWerkIdOrderByName(werkId);
        } else if (genreId != null) {
            return albumRepository.streamDistinctByTracksGenresIdOrderByName(genreId);
        } else if (interpretId != null) {
            return albumRepository.streamDistinctByTracksInterpretenIdOrderByName(interpretId);
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
