package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Track;
import com.cosmaslang.musicdataserver.db.entities.TrackDependentEntity;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
            page = trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumNameAscTracknumber(album, pageable);
        } else if (composer != null) {
            page = trackRepository.findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscTracknumber(composer, pageable);
        } else if (work != null) {
            page = trackRepository.findByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscTracknumber(work, pageable);
        } else if (genre != null) {
            //Page<Genre> genres = genreRepository.findByNameContaining(genre);
            page = trackRepository.findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscTracknumber(genre, pageable); //.findByGenresIsIn(new HashSet<>(genres));
        } else if (artist != null) {
            //Page<artist> artists = artistRepository.findByNameContaining(artist);
            //page = trackRepository.findByArtistsIsIn(new HashSet<>(artists));
            page = trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscTracknumber(artist, pageable);
        }

        logger.finer(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
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
            page = trackRepository.findByAlbumIdOrderByAlbumNameAscTracknumber(albumId, pageable);
        } else if (composerId != null) {
            page = trackRepository.findByComposerIdOrderByAlbumNameAscTracknumber(composerId, pageable);
        } else if (workId != null) {
            page = trackRepository.findByWorkIdOrderByAlbumNameAscTracknumber(workId, pageable);
        } else if (genreId != null) {
            page = trackRepository.findByGenresIdOrderByAlbumNameAscTracknumber(genreId, pageable);
        } else if (artistId != null) {
            page = trackRepository.findByArtistsIdOrderByAlbumNameAscTracknumber(artistId, pageable);
        }

        logger.finer(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
        return page;
    }

    @Override
    @Transactional
    protected void remove(@PathVariable Long id) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        trackRepository.delete(track);

        //Beim Löschen eines Tracks müssen wir selber auf evtl. entstehende Orphans achten
        removeFromTrackDependentParent(track, Track::getAlbum, albumRepository);
        removeFromTrackDependentParent(track, Track::getComposer, composerRepository);
        removeFromTrackDependentParent(track, Track::getWork, workRepository);
        removeFromTrackDependentParents(track, Track::getGenres, genreRepository);
        removeFromTrackDependentParents(track, Track::getArtists, artistRepository);
        /*
        //löscht ALLE Orphans, nicht nur die jetzt entstandenen
        deleteDependentParents(albumRepository);
        deleteDependentParents(composerRepository);
        deleteDependentParents(workRepository);
        deleteDependentParents(genreRepository);
        deleteDependentParents(artistRepository);
         */
    }

    private <E extends TrackDependentEntity>void deleteDependentParents(TrackDependentRepository<E> repository) {
        List<E> orphans = repository.findByTracksIsEmpty();
        repository.deleteAll(orphans);
    }
}

