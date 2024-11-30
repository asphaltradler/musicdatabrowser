package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/album")
public class AlbumRestController extends AbstractMusicDataRestController<Album> {
    @Override
    public Page<Album> find(Integer pagenumber, Integer pagesize,
                            String track, String album, String composer, String work, String genre, String artist) {
        logCall(pagenumber, pagesize, track, album, composer, work, genre, artist);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (track != null) {
            return albumRepository.findDistinctByTracksNameContainsIgnoreCase(track, pageable);
        } else if (album != null) {
            return albumRepository.findByNameContainsIgnoreCaseOrderByName(album, pageable);
        } else if (composer != null) {
            return albumRepository.findDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(composer, pageable);
        } else if (work != null) {
            return albumRepository.findDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(work, pageable);
        } else if (genre != null) {
            //"von Hand" wäre:
            //tracks = trackRepository.findByGenreLike(genre);
            //tracks.stream().map(Track::getAlbum).distinct().toList();
            //über spezielle Query:
            return albumRepository.findDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(genre, pageable);
        } else if (artist != null) {
            //tracks = trackRepository.findByArtistsLike(artist);
            //tracks.stream().map(Track::getAlbum).distinct().toList();
            return albumRepository.findDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(artist, pageable);
        }
        return Page.empty();
    }
    
    @Override
    public Page<Album> get(Integer pagenumber, Integer pagesize,
                                   Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logCall(pagenumber, pagesize, trackId, albumId, composerId, workId, genreId, artistId);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (albumId != null) {
            return albumRepository.findById(albumId, pageable);
        } else if (trackId != null) {
            return albumRepository.findByTracksId(trackId, pageable);
        } else if (composerId != null) {
            return albumRepository.findDistinctByTracksComposerIdOrderByName(composerId, pageable);
        } else if (workId != null) {
            return albumRepository.findDistinctByTracksWorkIdOrderByName(workId, pageable);
        } else if (genreId != null) {
            return albumRepository.findDistinctByTracksGenresIdOrderByName(genreId, pageable);
        } else if (artistId != null) {
            return albumRepository.findDistinctByTracksArtistsIdOrderByName(artistId, pageable);
        }
        return Page.empty();
    }

    @Override
    public Album getById(@PathVariable Long id) {
        return getById(id, albumRepository);
    }

    @Override
    public String remove(@RequestParam() Long id) {
        return remove(id, albumRepository);
    }
}
