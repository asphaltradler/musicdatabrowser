package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.NamedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface TrackDependentRepository<ENTITY extends NamedEntity> extends NamedEntityRepository<ENTITY> {
    Page<ENTITY> findDistinctByTracksNameContainsIgnoreCaseOrderByName(String track, Pageable pageable);
    Page<ENTITY> findDistinctByTracksIdOrderByName(Long trackId, Pageable pageable);
    Page<ENTITY> findDistinctByTracksAlbumNameContainsIgnoreCaseOrderByName(String track, Pageable pageable);
    Page<ENTITY> findDistinctByTracksAlbumIdOrderByName(Long trackId, Pageable pageable);
    Page<ENTITY> findDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(String composer, Pageable pageable);
    Page<ENTITY> findDistinctByTracksComposerIdOrderByName(Long composerId, Pageable pageable);
    Page<ENTITY> findDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(String genre, Pageable pageable);
    Page<ENTITY> findDistinctByTracksGenresIdOrderByName(Long genreId, Pageable pageable);
    Page<ENTITY> findDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(String artist, Pageable pageable);
    Page<ENTITY> findDistinctByTracksArtistsIdOrderByName(Long artistId, Pageable pageable);
    Page<ENTITY> findDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(String work, Pageable pageable);
    Page<ENTITY> findDistinctByTracksWorkIdOrderByName(Long workId, Pageable pageable);

    List<ENTITY> findByTracksIsEmpty();
    void deleteAllByTracksIsEmpty();
}
