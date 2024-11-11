package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Album;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface AlbumRepository extends NamedEntityRepository<Album> {
    Stream<Album> streamDistinctByTracksKomponistNameOrderByName(String komponist);
    Stream<Album> streamDistinctByTracksKomponistIdOrderByName(Long komponistId);
    Stream<Album> streamDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(String genre);
    Stream<Album> streamDistinctByTracksGenresIdOrderByName(Long genreId);
    Stream<Album> streamDistinctByTracksInterpretenNameContainsIgnoreCaseOrderByName(String interpret);
    Stream<Album> streamDistinctByTracksInterpretenIdOrderByName(Long interpretId);
    Stream<Album> streamDistinctByTracksWerkNameContainsIgnoreCaseOrderByName(String werk);
    Stream<Album> streamDistinctByTracksWerkIdOrderByName(Long werkId);
}
