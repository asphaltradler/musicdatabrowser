package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Interpret;
import com.cosmaslang.musikdataserver.db.entities.Track;
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
        if (interpret != null) {
            return interpretRepository.streamByNameContainsIgnoreCaseOrderByName(interpret);
        } else if (album != null) {
            return getInterpreten(trackRepository.streamByAlbum_NameContainsIgnoreCase(album));
        } else if (genre != null) {
            return getInterpreten(trackRepository.streamByGenres_NameContainsIgnoreCase(genre));
        } else if (komponist != null) {
            return getInterpreten(trackRepository.streamByKomponist_Name(komponist));
        }
        return getAll(interpretRepository);
    }

    @Override
    public Stream<Interpret> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getInterpreten(trackRepository.streamByAlbum_Id(albumId));
        } else if (komponistId != null) {
            return getInterpreten(trackRepository.streamByKomponist_Id(komponistId));
        } else if (werkId != null) {
            return getInterpreten(trackRepository.streamByWerk_Id(werkId));
        } else if (genreId != null) {
            return getInterpreten(trackRepository.streamByGenres_Id(genreId));
        } else if (interpretId != null) {
            return getEntitiesIfExists(interpretId, interpretRepository);
        }

        return getAll(interpretRepository);
    }

    private Stream<Interpret> getInterpreten(Stream<Track> tracks) {
        return getMappedByEntitySet(tracks, Track::getInterpreten);
    }
}
