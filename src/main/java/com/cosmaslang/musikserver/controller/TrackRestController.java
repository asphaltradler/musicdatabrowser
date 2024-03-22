package com.cosmaslang.musikserver.controller;

import com.cosmaslang.musikserver.db.entities.Track;
import com.cosmaslang.musikserver.db.repositories.TrackRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/musik/track")
public class TrackRestController extends AbstractMusikRestController<Track> {
    @Override
    public List<Track> get(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        List<Track> tracks;
        if (track != null) {
            tracks = trackRepository.findByTitle(track);
        } else if (album != null) {
            tracks = trackRepository.findByAlbumLike(album);
        } else if (komponist != null) {
            tracks = trackRepository.findByKomponist(komponist);
        } else if (werk != null) {
            tracks = trackRepository.findByWerkLike(werk);
        } else if (genre != null) {
            //List<Genre> genres = genreRepository.findByNameContaining(genre);
            tracks = trackRepository.findByGenreLike(genre); //.findByGenresIsIn(new HashSet<>(genres));
        } else if (interpret != null) {
            //List<Interpret> interpreten = interpretRepository.findByNameContaining(interpret);
            //tracks = trackRepository.findByInterpretenIsIn(new HashSet<>(interpreten));
            tracks = trackRepository.findByInterpretenLike(interpret);
        } else if (id != null) {
            tracks = getEntitiesIfExists(id, trackRepository);
        } else {
            tracks = getAll(trackRepository);
        }
        return tracks;
    }
    @Override
    public Track findById(@PathVariable Long id) {
        return getEntityIfExists(id, trackRepository);
    }

    @PostMapping("/create")
    public String create(@RequestBody Track track) {
        return add(track);
    }

    private String add(Track track) {
        String name = track.getTitle();
        List<Track> tracks = trackRepository.findByTitle(name);
        if (!tracks.isEmpty()) {
            track = tracks.get(0);
            return "track " + track + " already exists";
        } else {
            trackRepository.save(track);
            logger.info("added track " + track);
            return "added track " + track;
        }
    }

    @Override
    public String remove(@RequestParam() Long id) {
        Optional<Track> track = trackRepository.findById(id);
        //TODO was passiert mit Referenzen in interpreten_tracks usw.?
        if (track.isPresent()) {
            trackRepository.delete(track.get());
            return track + " removed";
        }
        return "Track " + id + " not found!";
    }
}
