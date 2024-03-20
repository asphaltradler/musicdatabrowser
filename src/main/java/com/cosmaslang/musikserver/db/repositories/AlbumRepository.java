package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Album;
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

    /*
    @Query("select a from Album a join Track t on t.album.id = a.id " +
            "where t.id in " +
            "(select t.id from Track t join fetch t.genres g where g.name like %:genre%)")
    List<Album> findByGenreLike(String genre);

    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.name = t.album.name " +
            "WHERE :interpret IN t.interpreten")
    List<Album> findByInterpret(Interpret interpret);
*/
    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.id = t.album.id " +
            "WHERE t.werk.name like %:werk%")
    List<Album> findByWerkLike(String werk);
}
