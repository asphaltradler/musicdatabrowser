package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/music/track")
public class TrackRestController extends AbstractMusicDataRestController<Track> {
    @Override
    public List<Track> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            return trackRepository.findByNameContainsIgnoreCaseOrderByName(track);
        } else if (album != null) {
            return trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(album);
        } else if (composer != null) {
            return trackRepository.findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer);
        } else if (work != null) {
            return trackRepository.findByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscId(work);
        } else if (genre != null) {
            //List<Genre> genres = genreRepository.findByNameContaining(genre);
            return trackRepository.findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre); //.findByGenresIsIn(new HashSet<>(genres));
        } else if (artist != null) {
            //List<artist> artists = artistRepository.findByNameContaining(artist);
            //return trackRepository.findByArtistsIsIn(new HashSet<>(artists));
            return trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist);
        }
        return getAll(trackRepository);
    }

    @Override
    public List<Track> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (trackId != null) {
            return getEntitiesIfExists(trackId, trackRepository);
        } else if (albumId != null) {
            return trackRepository.findByAlbumId(albumId);
        } else if (composerId != null) {
            return trackRepository.findByComposerId(composerId);
        } else if (workId != null) {
            return trackRepository.findByWorkId(workId);
        } else if (genreId != null) {
            return trackRepository.findByGenresId(genreId);
        } else if (artistId != null) {
            return trackRepository.findByArtistsId(artistId);
        }

        return getAll(trackRepository);
    }

    @Override
    public Track getById(@PathVariable Long id) {
        return getEntityIfExists(id, trackRepository);
    }

    @Override
    public String remove(@RequestParam() Long id) {
        Optional<Track> track = trackRepository.findById(id);
        //TODO was passiert mit Referenzen in artists_tracks usw.?
        if (track.isPresent()) {
            trackRepository.delete(track.get());
            return track + " removed";
        }
        return "Track " + id + " not found!";
    }
}

