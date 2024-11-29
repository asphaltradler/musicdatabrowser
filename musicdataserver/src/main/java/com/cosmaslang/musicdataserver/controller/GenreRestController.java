package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Genre;
import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/music/genre")
public class GenreRestController extends AbstractMusicDataRestController<Genre> {
    @Override
    public Genre getById(@PathVariable Long id) {
        return getEntityIfExists(id, genreRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected List<Genre> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            return getMappedTracks(trackRepository.findByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(album));
        } else if (composer != null) {
            return getMappedTracks(trackRepository.findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer));
        } else if (work != null) {
            return getMappedTracks(trackRepository.findByNameContainsIgnoreCaseOrderByName(work));
        } else if (genre != null) {
            return genreRepository.findByNameContainsIgnoreCaseOrderByName(genre);
        } else if (artist != null) {
            return getMappedTracks(trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist));
        }
        return getAll(genreRepository);
    }

    @Override
    public List<Genre> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId).stream().toList());
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.findByAlbumId(albumId));
        } else if (composerId != null) {
            return getMappedTracks(trackRepository.findByComposerId(composerId));
        } else if (workId != null) {
            return getMappedTracks(trackRepository.findByWorkId(workId));
        } else if (genreId != null) {
            return getEntitiesIfExists(genreId, genreRepository);
        } else if (artistId != null) {
            return getMappedTracks(trackRepository.findByArtistsId(artistId));
        }

        return getAll(genreRepository);
    }

    private List<Genre> getMappedTracks(List<Track> tracks) {
        return getMappedByEntitySet(tracks, Track::getGenres);
    }
}
