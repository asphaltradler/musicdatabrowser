package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Genre;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/musik/genre")
public class GenreRestController extends AbstractMusikRestController<Genre> {
    @Override
    public Genre findById(@PathVariable Long id) {
        return getEntityIfExists(id, genreRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected List<Genre> get(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        if (genre != null) {
            return genreRepository.findByNameContainingIgnoreCase(genre).stream().sorted().toList();
        } else if (album != null) {
            List<Track> tracks = trackRepository.findByAlbumLike(album);
            return tracks.stream().map(Track::getGenres).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
        } else if (komponist != null) {
            List<Track> tracks = trackRepository.findByKomponist(komponist);
            return tracks.stream().map(Track::getGenres).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
        } else if (interpret != null) {
            List<Track> tracks = trackRepository.findByInterpretenLike(interpret);
            return tracks.stream().map(Track::getGenres).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
        }
        return get(id, genreRepository);
    }
}
