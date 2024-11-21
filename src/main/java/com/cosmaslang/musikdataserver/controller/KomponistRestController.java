package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Komponist;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
@RequestMapping("/musik/komponist")
public class KomponistRestController extends AbstractMusikRestController<Komponist> {
    @Override
    public Komponist getById(@PathVariable Long id) {
        return getEntityIfExists(id, komponistRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected Stream<Komponist> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.logCall(track, album, komponist, werk, genre, interpret);
        if (track != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.streamByAlbumNameContainsIgnoreCaseOrderByAlbumName(album));
        } else if (komponist != null) {
            return komponistRepository.streamByNameContainsIgnoreCaseOrderByName(komponist);
        } else if (werk != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(werk));
        } else if (genre != null) {
            return getMappedTracks(trackRepository.streamDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre));
        } else if (interpret != null) {
            return getMappedTracks(trackRepository.streamDistinctByInterpretenNameContainsIgnoreCaseOrderByInterpretenNameAscAlbumNameAscId(interpret));
        }
        return getAll(komponistRepository);
    }

    @Override
    public Stream<Komponist> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId).stream());
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.streamByAlbumId(albumId));
        } else if (komponistId != null) {
            return komponistRepository.findById(komponistId).stream();
        } else if (werkId != null) {
            return getMappedTracks(trackRepository.streamByWerkId(werkId));
        } else if (genreId != null) {
            return getMappedTracks(trackRepository.streamByGenresId(genreId));
        } else if (interpretId != null) {
            return getMappedTracks(trackRepository.streamByInterpretenId(interpretId));
        }
        return getAll(komponistRepository);
    }

    private Stream<Komponist> getMappedTracks(Stream<Track> tracks) {
        return getMappedByEntity(tracks, Track::getKomponist);
    }
}
