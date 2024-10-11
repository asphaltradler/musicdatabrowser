package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Genre;
import com.cosmaslang.musikdataserver.db.entities.Interpret;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TrackRepository extends CrudRepository<Track, Long> {
    List<Track> findByTitle(String title);

    Track findByPath(String path);

    @Query("SELECT t FROM Track t WHERE t.album.name ilike %:album%")
    //identisch im Verhalten mit:
    //@Query("SELECT a.tracks FROM Album a WHERE a.name like %:album%")
    List<Track> findByAlbumLike(String album);

    @Query("SELECT t FROM Track t WHERE t.komponist.name = :komponist")
    List<Track> findByKomponist(String komponist);

    @Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    List<Track> findByGenreLike(String genre);
    List<Track> findByGenres(Genre genre);
    List<Track> findByGenresIsIn(Set<Genre> genres);

    @Query("select t from Track t join fetch t.interpreten i where i.name ilike %:interpret%")
    List<Track> findByInterpretenLike(String interpret);
    List<Track> findByInterpreten(Interpret interpret);
    List<Track> findByInterpretenIsIn(Set<Interpret> interpreten);

    @Query("SELECT t FROM Track t WHERE t.werk.name ILIKE %:werk%")
    List<Track> findByWerkLike(String werk);
}
