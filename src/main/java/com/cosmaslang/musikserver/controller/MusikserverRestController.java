package com.cosmaslang.musikserver.controller;

import com.cosmaslang.musikserver.db.entities.Album;
import com.cosmaslang.musikserver.db.entities.Genre;
import com.cosmaslang.musikserver.db.entities.Komponist;
import com.cosmaslang.musikserver.db.entities.Track;
import com.cosmaslang.musikserver.db.repositories.AlbumRepository;
import com.cosmaslang.musikserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/musik")
public class MusikserverRestController {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    TrackRepository trackRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    NamedEntityRepository<Komponist> komponistRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;

    @RequestMapping(value = "/track/get", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Track> getTracks(@RequestParam(required = false) String title,
                                 @RequestParam(required = false) String album,
                                 @RequestParam(required = false) String komponist,
                                 @RequestParam(required = false) String werk,
                                 @RequestParam(required = false) String genre,
                                 @RequestParam(required = false) Long id) {
        List<Track> tracks;
        if (StringUtils.hasLength(title)) {
            tracks = trackRepository.findByTitle(title);
        } else if (album != null) {
            tracks = trackRepository.findByAlbumLike(album);
        } else if (komponist != null) {
            tracks = trackRepository.findByKomponist(komponist);
        } else if (werk != null) {
            tracks = trackRepository.findByWerkLike(werk);
        } else if (genre != null) {
            tracks = trackRepository.findByGenre(genre);
        } else if (id != null) {
            tracks = Collections.singletonList(trackRepository.findById(id).orElse(null));
        } else {
            Iterator<Track> allTracks = trackRepository.findAll().iterator();
            tracks = new ArrayList<Track>();
            allTracks.forEachRemaining(tracks::add);
        }
        return tracks;
    }

    @GetMapping("/track/id/{id}")
    public Track findById(@PathVariable long id) {
        Optional<Track> track = trackRepository.findById(id);
        if (track.isPresent()) {
            return track.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Track found with id " + id);
    }

    @PostMapping("/track/create")
    public String createTrack(@RequestBody Track track) {
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

    @DeleteMapping("/track/remove")
    public String removeTrack(@RequestParam(required = true) Long id) {
        Optional<Track> track = trackRepository.findById(id);
        if (track.isPresent()) {
            trackRepository.delete(track.get());
            return track + " removed";
        }
        return "Track " + id + " not found!";
    }

    @RequestMapping(value = "/album/get", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Album> getAlben(@RequestParam(required = false) String album,
                                @RequestParam(required = false) String komponist,
                                @RequestParam(required = false) String werk,
                                @RequestParam(required = false) String genre,
                                @RequestParam(required = false) Long id) {
        List<Album> alben;
        if (album != null) {
            alben = Collections.singletonList(albumRepository.findByName(album));
        } else if (komponist != null) {
            alben = albumRepository.findByKomponist(komponist);
        } else if (werk != null) {
            alben = albumRepository.findByWerkLike(werk);
        } else if (genre != null) {
            alben = albumRepository.findByGenre(genre);
        } else if (id != null) {
            alben = Collections.singletonList(albumRepository.findById(id).orElse(null));
        } else {
            Iterator<Album> allAlben = albumRepository.findAll().iterator();
            alben = new ArrayList<>();
            allAlben.forEachRemaining(alben::add);
        }
        return alben;
    }
}
