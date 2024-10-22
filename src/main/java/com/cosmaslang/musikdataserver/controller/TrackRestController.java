package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/musik/track")
public class TrackRestController extends AbstractMusikRestController<Track> {
    @Override
    public List<Track> get(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        if (track != null) {
            return trackRepository.findByNameContainingIgnoreCase(track).stream().sorted().toList();
        } else if (album != null) {
            return trackRepository.findByAlbumLike(album).stream().sorted().toList();
        } else if (komponist != null) {
            return trackRepository.findByKomponist(komponist).stream().sorted().toList();
        } else if (werk != null) {
            return trackRepository.findByWerkLike(werk).stream().sorted().toList();
        } else if (genre != null) {
            //List<Genre> genres = genreRepository.findByNameContaining(genre);
            return trackRepository.findByGenreLike(genre).stream().sorted().toList(); //.findByGenresIsIn(new HashSet<>(genres));
        } else if (interpret != null) {
            //List<Interpret> interpreten = interpretRepository.findByNameContaining(interpret);
            //return trackRepository.findByInterpretenIsIn(new HashSet<>(interpreten));
            return trackRepository.findByInterpretenLike(interpret).stream().sorted().toList();
        } else if (id != null) {
            return getEntitiesIfExists(id, trackRepository);
        }
        return getAll(trackRepository);
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
        String name = track.getName();
        List<Track> tracks = trackRepository.findByNameContainingIgnoreCase(name);
        if (!tracks.isEmpty()) {
            track = tracks.get(0);
            return "track " + track + " already exists";
        } else {
            track = trackRepository.save(track);
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

