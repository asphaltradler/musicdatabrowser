package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Interpret;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    protected List<Interpret> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.find(track, album, komponist, werk, genre, interpret);
        if (interpret != null) {
            return interpretRepository.findByNameContainingIgnoreCase(interpret).stream().sorted().toList();
        } else if (album != null) {
            return getInterpreten(trackRepository.findByAlbumLike(album));
        } else if (genre != null) {
            return getInterpreten(trackRepository.findByGenreLike(genre));
        } else if (komponist != null) {
            return getInterpreten(trackRepository.findByKomponist(komponist));
        }
        return getAll(interpretRepository);
    }

    @Override
    public List<Interpret> get(@RequestParam(required = false) Long trackId,
                           @RequestParam(required = false) Long albumId,
                           @RequestParam(required = false) Long komponistId,
                           @RequestParam(required = false) Long werkId,
                           @RequestParam(required = false) Long genreId,
                           @RequestParam(required = false) Long interpretId) {
        super.get(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getInterpreten(trackRepository.findByAlbumId(albumId));
        } else if (komponistId != null) {
            return getInterpreten(trackRepository.findByKomponistId(komponistId));
        } else if (werkId != null) {
            return getInterpreten(trackRepository.findByWerkId(werkId));
        } else if (genreId != null) {
            return getInterpreten(trackRepository.findByGenreId(genreId));
        } else if (interpretId != null) {
            return getEntitiesIfExists(interpretId, interpretRepository);
        }

        return getAll(interpretRepository);
    }

    private List<Interpret> getInterpreten(List<Track> tracks) {
        return getFilteredByEntitySet(tracks, Track::getInterpreten);
    }
}
