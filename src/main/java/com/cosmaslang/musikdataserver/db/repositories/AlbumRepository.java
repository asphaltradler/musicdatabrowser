package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Album;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface AlbumRepository extends NamedEntityRepository<Album> {
    Stream<Album> streamDistinctByTracks_Komponist_NameOrderByName(String komponist);
    Stream<Album> streamDistinctByTracks_Komponist_IdOrderByName(Long komponistId);
    Stream<Album> streamDistinctByTracks_Genres_NameContainsIgnoreCaseOrderByName(String genre);
    Stream<Album> streamDistinctByTracks_Genres_IdOrderByName(Long genreId);
    Stream<Album> streamDistinctByTracks_Interpreten_NameContainsIgnoreCaseOrderByName(String interpret);
    Stream<Album> streamDistinctByTracks_Interpreten_IdOrderByName(Long interpretId);
    Stream<Album> streamDistinctByTracks_Werk_NameContainsIgnoreCaseOrderByName(String werk);
    Stream<Album> streamDistinctByTracks_Werk_IdOrderByName(Long werkId);
}
