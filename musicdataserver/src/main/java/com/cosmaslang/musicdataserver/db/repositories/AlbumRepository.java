package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends NamedEntityRepository<Album> {
    Page<Album> findDistinctByTracksNameContainsIgnoreCase(String track, Pageable pageable);
    Page<Album> findByTracksId(Long trackId, Pageable pageable);
    Page<Album> findDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(String composer, Pageable pageable);
    Page<Album> findDistinctByTracksComposerIdOrderByName(Long composerId, Pageable pageable);
    Page<Album> findDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(String genre, Pageable pageable);
    Page<Album> findDistinctByTracksGenresIdOrderByName(Long genreId, Pageable pageable);
    Page<Album> findDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(String artist, Pageable pageable);
    Page<Album> findDistinctByTracksArtistsIdOrderByName(Long artistId, Pageable pageable);
    Page<Album> findDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(String work, Pageable pageable);
    Page<Album> findDistinctByTracksWorkIdOrderByName(Long workId, Pageable pageable);
}
