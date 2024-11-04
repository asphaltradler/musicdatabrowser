package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Album;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends NamedEntityRepository<Album> {
    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.komponist.name = :komponist")
    List<Album> findByKomponist(String komponist);

    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.komponist.id = :komponistId")
    List<Album> findByKomponistId(Long komponistId);

    @Query("select a from Album a join Track t on a.id = t.album.id where t in " +
            "(select g.tracks from Genre g where g.name ilike %:genre%)")
    List<Album> findByGenreLike(String genre);

    @Query("select a from Album a join Track t on a.id = t.album.id where t in " +
            "(select g.tracks from Genre g where g.id = :genreId)")
    List<Album> findByGenreId(Long genreId);

    @Query("select a from Album a join Track t on a.id = t.album.id where t in " +
            "(select i.tracks from Interpret i where i.name ilike %:interpret%)")
    List<Album> findByInterpretLike(String interpret);

    @Query("select a from Album a join Track t on a.id = t.album.id where t in " +
            "(select i.tracks from Interpret i where i.id = :interpretId)")
    List<Album> findByInterpretId(Long interpretId);

    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.werk.name ilike %:werk%")
    List<Album> findByWerkLike(String werk);

    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.werk.id = :werkId")
    List<Album> findByWerkId(Long werkId);
}
