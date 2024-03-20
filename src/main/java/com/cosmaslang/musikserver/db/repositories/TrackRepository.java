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

    @Query("select t from Track t join fetch t.genres g where g.name like %:genre%")
    List<Track> findByGenreLike(String genre);
    //@Query("SELECT t FROM Track t JOIN genre_tracks gt ON t.genres  WHERE g IN (SELECT g FROM Genre where g.name = :genre)")
    List<Track> findByGenres(Genre genre);
    List<Track> findByGenresIsIn(Set<Genre> genres);

//    @Query("select t from Track t where t.id in" +
//            " (select it.track_id from interpreten_tracks it where it.interpret_id in" +
//            " (select i.id from Interpret i where i.name like %:interpret%))")
    @Query("select t from Track t join fetch t.interpreten i where i.name like %:interpret%")
    List<Track> findByInterpretenLike(String interpret);
    List<Track> findByInterpreten(Interpret interpret);
    List<Track> findByInterpretenIsIn(Set<Interpret> interpreten);

    @Query("SELECT t FROM Track t WHERE t.werk.name LIKE %:werk%")
    List<Track> findByWerkLike(String werk);
}
