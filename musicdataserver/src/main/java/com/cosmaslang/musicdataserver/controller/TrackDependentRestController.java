package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Track;
import com.cosmaslang.musicdataserver.db.entities.TrackDependentEntity;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@NoRepositoryBean
public abstract class TrackDependentRestController<ENTITY extends TrackDependentEntity> extends AbstractMusicDataRestController<ENTITY> {

    protected abstract TrackDependentRepository<ENTITY> getMyRepository();

    @Override
    public Page<ENTITY> findBy(Integer pageNumber, Integer pageSize,
                               String track, String album, String composer, String work, String genre, String artist) {
        logCall(pageNumber, pageSize, track, album, composer, work, genre, artist);

        Pageable pageable = getPageableOf(pageNumber, pageSize);
        Page<ENTITY> page = Page.empty(pageable);
        long time = System.currentTimeMillis();

        if (track != null) {
            page = getMyRepository().findDistinctByTracksNameContainsIgnoreCaseOrderByName(track, pageable);
        } else if (album != null) {
            page = getMyRepository().findDistinctByTracksAlbumNameContainsIgnoreCaseOrderByName(album, pageable);
        } else if (composer != null) {
            page = getMyRepository().findDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(composer, pageable);
        } else if (work != null) {
            page = getMyRepository().findDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(work, pageable);
        } else if (genre != null) {
            page = getMyRepository().findDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(genre, pageable);
        } else if (artist != null) {
            page = getMyRepository().findDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(artist, pageable);
        }

        logger.finer(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
        return page;
    }

    @Override
    public Page<ENTITY> get(Integer pageNumber, Integer pageSize,
                           Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logCall(pageNumber, pageSize, trackId, albumId, composerId, workId, genreId, artistId);

        Pageable pageable = getPageableOf(pageNumber, pageSize);
        Page<ENTITY> page = Page.empty(pageable);
        long time = System.currentTimeMillis();

        if (trackId != null) {
            page = getMyRepository().findDistinctByTracksIdOrderByName(trackId, pageable);
        } else if (albumId != null) {
            page = getMyRepository().findDistinctByTracksAlbumIdOrderByName(albumId, pageable);
        } else if (composerId != null) {
            page = getMyRepository().findDistinctByTracksComposerIdOrderByName(composerId, pageable);
        } else if (workId != null) {
            page = getMyRepository().findDistinctByTracksWorkIdOrderByName(workId, pageable);
        } else if (genreId != null) {
            page = getMyRepository().findDistinctByTracksGenresIdOrderByName(genreId, pageable);
        } else if (artistId != null) {
            page = getMyRepository().findDistinctByTracksArtistsIdOrderByName(artistId, pageable);
        }

        logger.finer(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
        return page;
    }

    public Page<ENTITY> getMappedPageByTracks(Page<Track> tracks,
                                                 Function<Track, Set<ENTITY>> mapFunction,
                                                 Pageable pageable) {
        //erst die Entities mappen und sortieren
        List<ENTITY> allEntitiesForTracks = tracks.stream().map(mapFunction).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
        //dann die passende Page ausschneiden
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allEntitiesForTracks.size());
        return new PageImpl<>(allEntitiesForTracks.subList(start, end), pageable, allEntitiesForTracks.size());
    }
}
