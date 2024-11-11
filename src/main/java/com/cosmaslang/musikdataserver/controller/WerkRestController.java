package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Track;
import com.cosmaslang.musikdataserver.db.entities.Werk;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

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
    protected Stream<Werk> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.logCall(track, album, komponist, werk, genre, interpret);
        if (werk != null) {
            return werkRepository.streamByNameContainsIgnoreCaseOrderByName(werk);
        } else if (album != null) {
            return getWerke(trackRepository.streamByAlbumNameContainsIgnoreCase(album));
        } else if (track != null) {
            return getWerke(trackRepository.streamByNameContainsIgnoreCaseOrderByName(track));
        } else if (genre != null) {
            return getWerke(trackRepository.streamByGenresNameContainsIgnoreCase(genre));
        } else if (interpret != null) {
            return getWerke(trackRepository.streamByInterpretenNameContainsIgnoreCase(interpret));
        }
        return getAll(werkRepository);
    }

    @Override
    public Stream<Werk> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getWerke(trackRepository.streamByAlbumId(albumId));
        } else if (trackId != null) {
            return getWerke(trackRepository.findById(trackId).stream());
        } else if (komponistId != null) {
            return getWerke(trackRepository.streamByKomponistId(komponistId));
        } else if (werkId != null) {
            return getEntitiesIfExists(werkId, werkRepository);
        } else if (genreId != null) {
            return getWerke(trackRepository.streamByGenresId(genreId));
        } else if (interpretId != null) {
            return getWerke(trackRepository.streamByInterpretenId(interpretId));
        }

        return getAll(werkRepository);
    }

    private Stream<Werk> getWerke(Stream<Track> tracks) {
        return getMappedByEntity(tracks, Track::getWerk);
    }
}
