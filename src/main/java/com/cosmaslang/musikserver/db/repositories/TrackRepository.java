package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends CrudRepository<Track, Long> {
    List<Track> findByTitle(String title);

    Track findByPath(String path);

    @Query("SELECT t FROM Track t WHERE t.album.name like %:album%")
    List<Track> findByAlbumLike(String album);

    @Query("SELECT t FROM Track t WHERE t.komponist.name = :komponist")
    List<Track> findByKomponist(String komponist);

    @Query("SELECT t FROM Track t WHERE t.genre.name = :genre")
    List<Track> findByGenre(String genre);

    @Query("SELECT t FROM Track t WHERE t.werk.name LIKE %:werk%")
    List<Track> findByWerkLike(String werk);
}
