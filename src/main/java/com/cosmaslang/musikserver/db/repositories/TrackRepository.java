package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Genre;
import com.cosmaslang.musikserver.db.entities.Interpret;
import com.cosmaslang.musikserver.db.entities.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TrackRepository extends CrudRepository<Track, Long> {
    List<Track> findByTitle(String title);

    Track findByPath(String path);

    @Query("SELECT t FROM Track t WHERE t.album.name like %:album%")
    List<Track> findByAlbumLike(String album);

    @Query("SELECT t FROM Track t WHERE t.komponist.name = :komponist")
    List<Track> findByKomponist(String komponist);

    //@Query("SELECT t FROM Track t JOIN genre_tracks gt ON t.genres  WHERE g IN (SELECT g FROM Genre where g.name = :genre)")
    List<Track> findByGenres(Genre genre);
    List<Track> findByGenresIsIn(Set<Genre> genres);

    List<Track> findByInterpreten(Interpret interpret);
    //@Query("SELECT t FROM interpreten_tracks it WHERE i IN (SELECT i FROM Interpret where i.name = :interpret)")
    List<Track> findByInterpretenIsIn(Set<Interpret> interpreten);

    @Query("SELECT t FROM Track t WHERE t.werk.name LIKE %:werk%")
    List<Track> findByWerkLike(String werk);
}
