package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Genre;
import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/genre")
public class GenreRestController extends AbstractMusicDataRestController<Genre> {
    @Override
    protected Page<Genre> find(Integer pagenumber, Integer pagesize, String track, String album, String composer, String work, String genre, String artist) {
        logCall(pagenumber, pagesize, track, album, composer, work, genre, artist);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (track != null) {
            return getMappedTracks(trackRepository.findByNameContainsIgnoreCaseOrderByName(track, pageable));
        } else if (album != null) {
            return getMappedTracks(trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(album, pageable));
        } else if (composer != null) {
            return getMappedTracks(trackRepository.findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer, pageable));
        } else if (work != null) {
            return getMappedTracks(trackRepository.findByNameContainsIgnoreCaseOrderByName(work, pageable));
        } else if (genre != null) {
            return genreRepository.findByNameContainsIgnoreCaseOrderByName(genre, pageable);
        } else if (artist != null) {
            return getMappedTracks(trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist, pageable));
        }
        return Page.empty();
    }

    @Override
    public Page<Genre> get(Integer pagenumber, Integer pagesize, Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logCall(pagenumber, pagesize, trackId, albumId, composerId, workId, genreId, artistId);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId, pageable));
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.findByAlbumId(albumId, pageable));
        } else if (composerId != null) {
            return getMappedTracks(trackRepository.findByComposerId(composerId, pageable));
        } else if (workId != null) {
            return getMappedTracks(trackRepository.findByWorkId(workId, pageable));
        } else if (genreId != null) {
            return genreRepository.findById(genreId, pageable);
        } else if (artistId != null) {
            return getMappedTracks(trackRepository.findByArtistsId(artistId, pageable));
        }
        return Page.empty();
    }

    private Page<Genre> getMappedTracks(Page<Track> tracks) {
        return getMappedByEntitySet(tracks, Track::getGenres);
    }

    @Override
    public Genre getById(@PathVariable Long id) {
        return getById(id, genreRepository);
    }

    @Override
    protected String remove(Long id) {
        return remove(id, genreRepository);
    }
}
