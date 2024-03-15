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
            "ON a.name = t.album.name " +
            "WHERE t.komponist.name = :komponist")
    List<Album> findByKomponist(String komponist);

    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.name = t.album.name " +
            "WHERE t.genre.name = :genre")
    List<Album> findByGenre(String genre);

    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.name = t.album.name " +
            "WHERE t.werk.name like %:werk%")
    List<Album> findByWerkLike(String werk);

}
