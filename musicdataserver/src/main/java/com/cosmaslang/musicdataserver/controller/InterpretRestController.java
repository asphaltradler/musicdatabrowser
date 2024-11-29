package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Interpret;
import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
@RequestMapping("/musik/interpret")
public class InterpretRestController extends AbstractMusikRestController<Interpret> {
    @Override
    public Interpret getById(@PathVariable Long id) {
        return getEntityIfExists(id, interpretRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected Stream<Interpret> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.logCall(track, album, komponist, werk, genre, interpret);
        if (track != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.streamByAlbumNameContainsIgnoreCaseOrderByAlbumName(album));
        } else if (komponist != null) {
            return getMappedTracks(trackRepository.streamByKomponistNameContainsIgnoreCaseOrderByKomponistNameAscAlbumNameAscId(komponist));
        } else if (werk != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(werk));
        } else if (genre != null) {
            return getMappedTracks(trackRepository.streamDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre));
        } else if (interpret != null) {
            return interpretRepository.streamByNameContainsIgnoreCaseOrderByName(interpret);
        }
        return getAll(interpretRepository);
    }

    @Override
    public Stream<Interpret> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId).stream());
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.streamByAlbumId(albumId));
        } else if (komponistId != null) {
            return getMappedTracks(trackRepository.streamByKomponistId(komponistId));
        } else if (werkId != null) {
            return getMappedTracks(trackRepository.streamByWerkId(werkId));
        } else if (genreId != null) {
            return getMappedTracks(trackRepository.streamByGenresId(genreId));
        } else if (interpretId != null) {
            return getEntitiesIfExists(interpretId, interpretRepository);
        }
        return getAll(interpretRepository);
    }

    private Stream<Interpret> getMappedTracks(Stream<Track> tracks) {
        return getMappedByEntitySet(tracks, Track::getInterpreten);
    }
}
