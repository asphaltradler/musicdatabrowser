package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Album;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface AlbumRepository extends NamedEntityRepository<Album> {
    @Query("SELECT DISTINCT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.komponist.name = :komponist")
    Stream<Album> streamByTracks_Komponist_Name(String komponist);

    @Query("SELECT DISTINCT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.komponist.id = :komponistId")
    Stream<Album> streamByTracks_Komponist_Id(Long komponistId);

    @Query("select distinct a from Album a join Track t on a.id = t.album.id where t in " +
            "(select g.tracks from Genre g where g.name ilike %:genre%)")
    Stream<Album> streamByTracks_Genres_NameContainsIgnoreCase(String genre);

    @Query("select distinct a from Album a join Track t on a.id = t.album.id where t in " +
            "(select g.tracks from Genre g where g.id = :genreId)")
    Stream<Album> streamByTracks_Genres_Id(Long genreId);

    @Query("select distinct a from Album a join Track t on a.id = t.album.id where t in " +
            "(select i.tracks from Interpret i where i.name ilike %:interpret%)")
    Stream<Album> streamByTracks_Interpreten_NameContainsIgnoreCase(String interpret);

    @Query("select distinct a from Album a join Track t on a.id = t.album.id where t in " +
            "(select i.tracks from Interpret i where i.id = :interpretId)")
    Stream<Album> streamByTracks_Interpreten_Id(Long interpretId);

    @Query("SELECT distinct a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.werk.name ilike %:werk%")
    Stream<Album> streamByTracks_Werke_NameContainsIgnoreCase(String werk);

    @Query("SELECT distinct a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.werk.id = :werkId")
    Stream<Album> streamByTracks_Werke_Id(Long werkId);
}
