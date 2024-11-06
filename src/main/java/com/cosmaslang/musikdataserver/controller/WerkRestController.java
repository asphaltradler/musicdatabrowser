package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Track;
import com.cosmaslang.musikdataserver.db.entities.Werk;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/musik/werk")
public class WerkRestController extends AbstractMusikRestController<Werk> {
    @Override
    public Werk getById(@PathVariable Long id) {
        return getEntityIfExists(id, werkRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected List<Werk> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.find(track, album, komponist, werk, genre, interpret);
        if (werk != null) {
            return werkRepository.findByNameContainingIgnoreCase(werk).stream().sorted().toList();
        } else if (album != null) {
            return getWerke(trackRepository.findByAlbumLike(album));
        } else if (genre != null) {
            return getWerke(trackRepository.findByGenreLike(genre));
        } else if (interpret != null) {
            return getWerke(trackRepository.findByInterpretenLike(interpret));
        }
        return getAll(werkRepository);
    }

    @Override
    public List<Werk> get(@RequestParam(required = false) Long trackId,
                               @RequestParam(required = false) Long albumId,
                               @RequestParam(required = false) Long komponistId,
                               @RequestParam(required = false) Long werkId,
                               @RequestParam(required = false) Long genreId,
                               @RequestParam(required = false) Long interpretId) {
        super.get(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getWerke(trackRepository.findByAlbumId(albumId));
        } else if (komponistId != null) {
            return getWerke(trackRepository.findByKomponistId(komponistId));
        } else if (werkId != null) {
            return getEntitiesIfExists(werkId, werkRepository);
        } else if (genreId != null) {
            return getWerke(trackRepository.findByGenreId(genreId));
        } else if (interpretId != null) {
            return getWerke(trackRepository.findByInterpretId(interpretId));
        }

        return getAll(werkRepository);
    }

    private List<Werk> getWerke(List<Track> tracks) {
        return getFilteredByEntity(tracks, Track::getWerk);
    }
}
