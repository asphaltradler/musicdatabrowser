package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Genre;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    protected List<Genre> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.find(track, album, komponist, werk, genre, interpret);
        if (genre != null) {
            return genreRepository.findByNameContainingIgnoreCase(genre).stream().sorted().toList();
        } else if (album != null) {
            return getGenres(trackRepository.findByAlbumLike(album));
        } else if (komponist != null) {
            return getGenres(trackRepository.findByKomponist(komponist));
        } else if (interpret != null) {
            return getGenres(trackRepository.findByInterpretenLike(interpret));
        }
        return getAll(genreRepository);
    }

    @Override
    public List<Genre> get(@RequestParam(required = false) Long trackId,
                               @RequestParam(required = false) Long albumId,
                               @RequestParam(required = false) Long komponistId,
                               @RequestParam(required = false) Long werkId,
                               @RequestParam(required = false) Long genreId,
                               @RequestParam(required = false) Long interpretId) {
        super.get(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getGenres(trackRepository.findByAlbumId(albumId));
        } else if (komponistId != null) {
            return getGenres(trackRepository.findByKomponistId(komponistId));
        } else if (werkId != null) {
            return getGenres(trackRepository.findByWerkId(werkId));
        } else if (genreId != null) {
            return getEntitiesIfExists(genreId, genreRepository);
        } else if (interpretId != null) {
            return getGenres(trackRepository.findByInterpretId(interpretId));
        }

        return getAll(genreRepository);
    }

    private List<Genre> getGenres(List<Track> tracks) {
        return getFilteredByEntitySet(tracks, Track::getGenres);
    }
}
