package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/musik/track")
public class TrackRestController extends AbstractMusikRestController<Track> {
    @Override
    public Stream<Track> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.logCall(track, album, komponist, werk, genre, interpret);
        if (track != null) {
            return trackRepository.streamByNameContainsIgnoreCaseOrderByName(track);
        } else if (album != null) {
            return trackRepository.streamByAlbumNameContainsIgnoreCaseOrderByAlbumName(album);
        } else if (komponist != null) {
            return trackRepository.streamByKomponistNameContainsIgnoreCaseOrderByKomponistNameAscAlbumNameAscId(komponist);
        } else if (werk != null) {
            return trackRepository.streamByWerkNameContainsIgnoreCaseOrderByWerkNameAscAlbumNameAscId(werk);
        } else if (genre != null) {
            //Stream<Genre> genres = genreRepository.findByNameContaining(genre);
            return trackRepository.streamDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre); //.streamByGenresIsIn(new HashSet<>(genres));
        } else if (interpret != null) {
            //Stream<Interpret> interpreten = interpretRepository.findByNameContaining(interpret);
            //return trackRepository.streamByInterpretenIsIn(new HashSet<>(interpreten));
            return trackRepository.streamDistinctByInterpretenNameContainsIgnoreCaseOrderByInterpretenNameAscAlbumNameAscId(interpret);
        }
        return getAll(trackRepository);
    }

    @Override
    public Stream<Track> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (trackId != null) {
            return getEntitiesIfExists(trackId, trackRepository);
        } else if (albumId != null) {
            return trackRepository.streamByAlbumId(albumId);
        } else if (komponistId != null) {
            return trackRepository.streamByKomponistId(komponistId);
        } else if (werkId != null) {
            return trackRepository.streamByWerkId(werkId);
        } else if (genreId != null) {
            return trackRepository.streamByGenresId(genreId);
        } else if (interpretId != null) {
            return trackRepository.streamByInterpretenId(interpretId);
        }

        return getAll(trackRepository);
    }

    @Override
    public Track getById(@PathVariable Long id) {
        return getEntityIfExists(id, trackRepository);
    }

    @Override
    public String remove(@RequestParam() Long id) {
        Optional<Track> track = trackRepository.findById(id);
        //TODO was passiert mit Referenzen in interpreten_tracks usw.?
        if (track.isPresent()) {
            trackRepository.delete(track.get());
            return track + " removed";
        }
        return "Track " + id + " not found!";
    }
}
