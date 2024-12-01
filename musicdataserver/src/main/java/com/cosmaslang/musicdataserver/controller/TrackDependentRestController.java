package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.NamedEntity;
import com.cosmaslang.musicdataserver.db.entities.Track;
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
public abstract class TrackDependentRestController<ENTITY extends NamedEntity> extends AbstractMusicDataRestController<ENTITY> {

    protected abstract TrackDependentRepository<ENTITY> getMyRepository();

    @Override
    public Page<ENTITY> find(Integer pagenumber, Integer pagesize,
                            String track, String album, String composer, String work, String genre, String artist) {
        logCall(pagenumber, pagesize, track, album, composer, work, genre, artist);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (track != null) {
            return getMyRepository().findDistinctByTracksNameContainsIgnoreCase(track, pageable);
        } else if (album != null) {
            return getMyRepository().findDistinctByTracksAlbumNameContainsIgnoreCase(album, pageable);
        } else if (composer != null) {
            return getMyRepository().findDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(composer, pageable);
        } else if (work != null) {
            return getMyRepository().findDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(work, pageable);
        } else if (genre != null) {
            return getMyRepository().findDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(genre, pageable);
        } else if (artist != null) {
            return getMyRepository().findDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(artist, pageable);
        }
        return Page.empty(pageable);
    }

    @Override
    public Page<ENTITY> get(Integer pagenumber, Integer pagesize,
                           Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logCall(pagenumber, pagesize, trackId, albumId, composerId, workId, genreId, artistId);
        Pageable pageable = getPageableOf(pagenumber, pagesize);
        if (trackId != null) {
            return getMyRepository().findByTracksId(trackId, pageable);
        } else if (albumId != null) {
            return getMyRepository().findDistinctByTracksAlbumIdOrderByName(albumId, pageable);
        } else if (composerId != null) {
            return getMyRepository().findDistinctByTracksComposerIdOrderByName(composerId, pageable);
        } else if (workId != null) {
            return getMyRepository().findDistinctByTracksWorkIdOrderByName(workId, pageable);
        } else if (genreId != null) {
            return getMyRepository().findDistinctByTracksGenresIdOrderByName(genreId, pageable);
        } else if (artistId != null) {
            return getMyRepository().findDistinctByTracksArtistsIdOrderByName(artistId, pageable);
        }
        return Page.empty(pageable);
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
