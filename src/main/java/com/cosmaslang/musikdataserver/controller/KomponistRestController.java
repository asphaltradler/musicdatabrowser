package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Komponist;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    protected Stream<Komponist> find(String track, String album, String komponist, String werk, String genre, String interpret) {
        super.logCall(track, album, komponist, werk, genre, interpret);
        if (komponist != null) {
            return komponistRepository.streamByNameContainsIgnoreCaseOrderByName(komponist);
        } else if (album != null) {
            //Hier können mehrere Komponisten erscheinen, da die Zuordnung track-weise ist.
            //Außerdem können durch "like" ja mehrere Alben gefunden werden.
            //Das ganze könnte man alternativ auch wie in AlbumRepository/Controller über
            //eigene Queries mit JOIN machen
            return getKomponisten(trackRepository.streamByAlbumNameContainsIgnoreCase(album));
        } else if (track != null) {
            return getKomponisten(trackRepository.streamByNameContainsIgnoreCaseOrderByName(track));
        } else if (genre != null) {
            return getKomponisten(trackRepository.streamByGenresNameContainsIgnoreCase(genre));
        } else if (interpret != null) {
            return getKomponisten(trackRepository.streamByInterpretenNameContainsIgnoreCase(interpret));
        }
        return getAll(komponistRepository);
    }

    @Override
    public Stream<Komponist> get(Long trackId, Long albumId, Long komponistId, Long werkId, Long genreId, Long interpretId) {
        super.logCall(trackId, albumId, komponistId, werkId, genreId, interpretId);
        if (albumId != null) {
            return getKomponisten(trackRepository.streamByAlbumId(albumId));
        } else if (trackId != null) {
            return getKomponisten(trackRepository.findById(trackId).stream());
        } else if (komponistId != null) {
            return getEntitiesIfExists(komponistId, komponistRepository);
        } else if (werkId != null) {
            return getKomponisten(trackRepository.streamByWerkId(werkId));
        } else if (genreId != null) {
            return getKomponisten(trackRepository.streamByGenresId(genreId));
        } else if (interpretId != null) {
            return getKomponisten(trackRepository.streamByInterpretenId(interpretId));
        }

        return getAll(komponistRepository);
    }

    private Stream<Komponist> getKomponisten(Stream<Track> tracks) {
        return getMappedByEntity(tracks, Track::getKomponist);
    }
}
