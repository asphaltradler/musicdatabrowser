package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Komponist;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
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
    protected List<Komponist> find(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        super.find(track, album, komponist, werk, genre, interpret, id);
        if (komponist != null) {
            return komponistRepository.findByNameContainingIgnoreCase(komponist).stream().sorted().toList();
        } else if (album != null) {
            //Hier können mehrere Komponisten erscheinen, da die Zuordnung track-weise ist.
            //Außerdem können durch "like" ja mehrere Alben gefunden werden.
            //Das ganze könnte man alternativ auch wie in AlbumRepository/Controller über
            //eigene Queries mit JOIN machen
            List<Track> tracks = trackRepository.findByAlbumLike(album);
            Stream<Komponist> komponistStream = tracks.stream().map(Track::getKomponist).filter(Objects::nonNull);
            return komponistStream.distinct().sorted().toList();
        } else if (genre != null) {
            List<Track> tracks = trackRepository.findByGenreLike(genre);
            return tracks.stream().map(Track::getKomponist).filter(Objects::nonNull).distinct().sorted().toList();
        } else if (interpret != null) {
            List<Track> tracks = trackRepository.findByInterpretenLike(interpret);
            return tracks.stream().map(Track::getKomponist).filter(Objects::nonNull).distinct().sorted().toList();
        }
        return get(id, komponistRepository);
    }
}
