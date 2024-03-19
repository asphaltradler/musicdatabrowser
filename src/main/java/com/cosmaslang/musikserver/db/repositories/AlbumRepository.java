package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Album;
import com.cosmaslang.musikserver.db.entities.Genre;
import com.cosmaslang.musikserver.db.entities.Interpret;
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

    /*
    @Query("SELECT a " +
            "FROM Album a " +
            "JOIN Track t " +
            "ON a.name = t.album.name " +
            "WHERE :genre IN t.genres")
    List<Album> findByGenre(Genre genre);

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
            "ON a.name = t.album.name " +
            "WHERE t.werk.name like %:werk%")
    List<Album> findByWerkLike(String werk);
}
