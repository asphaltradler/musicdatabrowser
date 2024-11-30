package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/track")
public class TrackRestController extends AbstractMusicDataRestController<Track> {
    @Override
    public Page<Track> find(Integer pagenumber, Integer pagesize, String track, String album, String composer, String work, String genre, String artist) {
        logCall(pagenumber, pagesize, track, album, composer, work, genre, artist);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (track != null) {
            return trackRepository.findByNameContainsIgnoreCaseOrderByName(track, pageable);
        } else if (album != null) {
            return trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(album, pageable);
        } else if (composer != null) {
            return trackRepository.findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer, pageable);
        } else if (work != null) {
            return trackRepository.findByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscId(work, pageable);
        } else if (genre != null) {
            //Page<Genre> genres = genreRepository.findByNameContaining(genre);
            return trackRepository.findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre, pageable); //.findByGenresIsIn(new HashSet<>(genres));
        } else if (artist != null) {
            //Page<artist> artists = artistRepository.findByNameContaining(artist);
            //return trackRepository.findByArtistsIsIn(new HashSet<>(artists));
            return trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist, pageable);
        }
        return Page.empty();
    }

    @Override
    public Page<Track> get(Integer pagenumber, Integer pagesize, Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logCall(pagenumber, pagesize, trackId, albumId, composerId, workId, genreId, artistId);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (trackId != null) {
            return trackRepository.findById(trackId, pageable);
        } else if (albumId != null) {
            return trackRepository.findByAlbumId(albumId, pageable);
        } else if (composerId != null) {
            return trackRepository.findByComposerId(composerId, pageable);
        } else if (workId != null) {
            return trackRepository.findByWorkId(workId, pageable);
        } else if (genreId != null) {
            return trackRepository.findByGenresId(genreId, pageable);
        } else if (artistId != null) {
            return trackRepository.findByArtistsId(artistId, pageable);
        }
        return Page.empty();
    }

    @Override
    public Track getById(@PathVariable Long id) {
        return getById(id, trackRepository);
    }

    @Override
    public String remove(@RequestParam() Long id) {
        return remove(id, trackRepository);
    }
}

