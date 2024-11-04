package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Interpret;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    protected List<Interpret> find(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        super.find(track, album, komponist, werk, genre, interpret, id);
        if (interpret != null) {
            return interpretRepository.findByNameContainingIgnoreCase(interpret).stream().sorted().toList();
        } else if (album != null) {
            List<Track> tracks = trackRepository.findByAlbumLike(album);
            return tracks.stream().map(Track::getInterpreten).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
        } else if (genre != null) {
            List<Track> tracks = trackRepository.findByGenreLike(genre);
            return tracks.stream().map(Track::getInterpreten).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
        } else if (komponist != null) {
            List<Track> tracks = trackRepository.findByKomponist(komponist);
            return tracks.stream().map(Track::getInterpreten).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
        }
        return get(id, interpretRepository);
    }
}
