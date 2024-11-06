package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Komponist;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    protected List<Komponist> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.find(track, album, komponist, werk, genre, interpret);
        if (komponist != null) {
            return komponistRepository.findByNameContainingIgnoreCase(komponist).stream().sorted().toList();
        } else if (album != null) {
            //Hier können mehrere Komponisten erscheinen, da die Zuordnung track-weise ist.
            //Außerdem können durch "like" ja mehrere Alben gefunden werden.
            //Das ganze könnte man alternativ auch wie in AlbumRepository/Controller über
            //eigene Queries mit JOIN machen
            return getKomponisten(trackRepository.findByAlbumLike(album));
        } else if (genre != null) {
            return getKomponisten(trackRepository.findByGenreLike(genre));
        } else if (interpret != null) {
            return getKomponisten(trackRepository.findByInterpretenLike(interpret));
        }
        return getAll(komponistRepository);
    }

    @Override
    public List<Komponist> get(@RequestParam(required = false) Long trackId,
                               @RequestParam(required = false) Long albumId,
                               @RequestParam(required = false) Long komponistId,
                               @RequestParam(required = false) Long werkId,
                               @RequestParam(required = false) Long genreId,
                               @RequestParam(required = false) Long interpretId) {
        super.get(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getKomponisten(trackRepository.findByAlbumId(albumId));
        } else if (komponistId != null) {
            return getEntitiesIfExists(komponistId, komponistRepository);
        } else if (werkId != null) {
            return getKomponisten(trackRepository.findByWerkId(werkId));
        } else if (genreId != null) {
            return getKomponisten(trackRepository.findByGenreId(genreId));
        } else if (interpretId != null) {
            return getKomponisten(trackRepository.findByInterpretId(interpretId));
        }

        return getAll(komponistRepository);
    }

    private List<Komponist> getKomponisten(List<Track> tracks) {
        return getFilteredByEntity(tracks, Track::getKomponist);
    }
}
