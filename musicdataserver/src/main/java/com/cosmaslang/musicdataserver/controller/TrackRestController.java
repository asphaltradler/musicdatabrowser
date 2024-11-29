package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/music/track")
public class TrackRestController extends AbstractMusicDataRestController<Track> {
    @Override
    public Stream<Track> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            return trackRepository.streamByNameContainsIgnoreCaseOrderByName(track);
        } else if (album != null) {
            return trackRepository.streamByAlbumNameContainsIgnoreCaseOrderByAlbumName(album);
        } else if (composer != null) {
            return trackRepository.streamByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer);
        } else if (work != null) {
            return trackRepository.streamByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscId(work);
        } else if (genre != null) {
            //Stream<Genre> genres = genreRepository.findByNameContaining(genre);
            return trackRepository.streamDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre); //.streamByGenresIsIn(new HashSet<>(genres));
        } else if (artist != null) {
            //Stream<Artist> artists = artistRepository.findByNameContaining(artist);
            //return trackRepository.streamByArtistsIsIn(new HashSet<>(artists));
            return trackRepository.streamDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist);
        }
        return getAll(trackRepository);
    }

    @Override
    public Stream<Track> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (trackId != null) {
            return getEntitiesIfExists(trackId, trackRepository);
        } else if (albumId != null) {
            return trackRepository.streamByAlbumId(albumId);
        } else if (composerId != null) {
            return trackRepository.streamByComposerId(composerId);
        } else if (workId != null) {
            return trackRepository.streamByWorkId(workId);
        } else if (genreId != null) {
            return trackRepository.streamByGenresId(genreId);
        } else if (artistId != null) {
            return trackRepository.streamByArtistsId(artistId);
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
        //TODO was passiert mit Referenzen in artist_tracks usw.?
        if (track.isPresent()) {
            trackRepository.delete(track.get());
            return track + " removed";
        }
        return "Track " + id + " not found!";
    }
}

