package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Track;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music/track")
public class TrackRestController extends AbstractMusicDataRestController<Track> {
    @Override
    protected NamedEntityRepository<Track> getMyRepository() {
        return trackRepository;
    }

    @Override
    public Page<Track> findBy(Integer pageNumber, Integer pageSize, String track, String album, String composer, String work, String genre, String artist) {
        logCall(pageNumber, pageSize, track, album, composer, work, genre, artist);

        Pageable pageable = getPageableOf(pageNumber, pageSize);
        Page<Track> page = Page.empty(pageable);
        long time = System.currentTimeMillis();

        if (track != null) {
            page = trackRepository.findByNameContainsIgnoreCaseOrderByName(track, pageable);
        } else if (album != null) {
            page = trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(album, pageable);
        } else if (composer != null) {
            page = trackRepository.findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer, pageable);
        } else if (work != null) {
            page = trackRepository.findByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscId(work, pageable);
        } else if (genre != null) {
            //Page<Genre> genres = genreRepository.findByNameContaining(genre);
            page = trackRepository.findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre, pageable); //.findByGenresIsIn(new HashSet<>(genres));
        } else if (artist != null) {
            //Page<artist> artists = artistRepository.findByNameContaining(artist);
            //page = trackRepository.findByArtistsIsIn(new HashSet<>(artists));
            page = trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist, pageable);
        }

        logger.info(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
        return page;
    }

    @Override
    public Page<Track> get(Integer pageNumber, Integer pageSize, Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logCall(pageNumber, pageSize, trackId, albumId, composerId, workId, genreId, artistId);

        Pageable pageable = getPageableOf(pageNumber, pageSize);
        Page<Track> page = Page.empty(pageable);
        long time = System.currentTimeMillis();

        if (trackId != null) {
            page = trackRepository.findById(trackId, pageable);
        } else if (albumId != null) {
            page = trackRepository.findByAlbumId(albumId, pageable);
        } else if (composerId != null) {
            page = trackRepository.findByComposerId(composerId, pageable);
        } else if (workId != null) {
            page = trackRepository.findByWorkId(workId, pageable);
        } else if (genreId != null) {
            page = trackRepository.findByGenresId(genreId, pageable);
        } else if (artistId != null) {
            page = trackRepository.findByArtistsId(artistId, pageable);
        }

        logger.info(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
        return page;
    }
}

