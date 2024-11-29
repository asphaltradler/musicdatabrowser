package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface AlbumRepository extends NamedEntityRepository<Album> {
    Page<Album> findDistinctByTracksNameContainsIgnoreCase(String track, Pageable pageable);
    Stream<Album> streamByTracksId(Long trackId);
    Stream<Album> streamDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(String composer);
    Stream<Album> streamDistinctByTracksComposerIdOrderByName(Long composerId);
    Stream<Album> streamDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(String genre);
    Stream<Album> streamDistinctByTracksGenresIdOrderByName(Long genreId);
    Stream<Album> streamDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(String artist);
    Stream<Album> streamDistinctByTracksArtistsIdOrderByName(Long artistId);
    Stream<Album> streamDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(String work);
    Stream<Album> streamDistinctByTracksWorkIdOrderByName(Long workId);
}
