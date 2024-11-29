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
    Stream<Album> streamDistinctByTracksKomponistNameContainsIgnoreCaseOrderByName(String komponist);
    Stream<Album> streamDistinctByTracksKomponistIdOrderByName(Long komponistId);
    Stream<Album> streamDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(String genre);
    Stream<Album> streamDistinctByTracksGenresIdOrderByName(Long genreId);
    Stream<Album> streamDistinctByTracksInterpretenNameContainsIgnoreCaseOrderByName(String interpret);
    Stream<Album> streamDistinctByTracksInterpretenIdOrderByName(Long interpretId);
    Stream<Album> streamDistinctByTracksWerkNameContainsIgnoreCaseOrderByName(String werk);
    Stream<Album> streamDistinctByTracksWerkIdOrderByName(Long werkId);
}
