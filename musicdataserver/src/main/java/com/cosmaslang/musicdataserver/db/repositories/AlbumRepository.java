package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface AlbumRepository extends NamedEntityRepository<Album> {
    Page<Album> findDistinctByTracksNameContainsIgnoreCase(String track, Pageable pageable);
    List<Album> findByTracksId(Long trackId);
    List<Album> findDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(String composer);
    List<Album> findDistinctByTracksComposerIdOrderByName(Long composerId);
    List<Album> findDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(String genre);
    List<Album> findDistinctByTracksGenresIdOrderByName(Long genreId);
    List<Album> findDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(String artist);
    List<Album> findDistinctByTracksArtistsIdOrderByName(Long artistId);
    List<Album> findDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(String work);
    List<Album> findDistinctByTracksWorkIdOrderByName(Long workId);
}
