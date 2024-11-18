package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Genre;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
@RequestMapping("/musik/genre")
public class GenreRestController extends AbstractMusikRestController<Genre> {
    @Override
    public Genre getById(@PathVariable Long id) {
        return getEntityIfExists(id, genreRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected Stream<Genre> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.logCall(track, album, komponist, werk, genre, interpret);
        if (track != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.streamByAlbumNameContainsIgnoreCase(album));
        } else if (komponist != null) {
            return getMappedTracks(trackRepository.streamByKomponistNameContainsIgnoreCaseOrderByName(komponist));
        } else if (werk != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(werk));
        } else if (genre != null) {
            return genreRepository.streamByNameContainsIgnoreCaseOrderByName(genre);
        } else if (interpret != null) {
            return getMappedTracks(trackRepository.streamByInterpretenNameContainsIgnoreCase(interpret));
        }
        return getAll(genreRepository);
    }

    @Override
    public Stream<Genre> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
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
            return getEntitiesIfExists(genreId, genreRepository);
        } else if (interpretId != null) {
            return getMappedTracks(trackRepository.streamByInterpretenId(interpretId));
        }

        return getAll(genreRepository);
    }

    private Stream<Genre> getMappedTracks(Stream<Track> tracks) {
        return getMappedByEntitySet(tracks, Track::getGenres);
    }
}
