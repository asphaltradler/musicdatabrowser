package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Genre;
import com.cosmaslang.musikdataserver.db.entities.Interpret;
import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TrackRepository extends NamedEntityRepository<Track> {
    Track findByPath(String path);

    @Query("SELECT t FROM Track t WHERE t.album.name ilike %:album%")
        //identisch im Verhalten mit:
        //@Query("SELECT a.tracks FROM Album a WHERE a.name like %:album%")
    List<Track> findByAlbumLike(String album);

    @Query("SELECT t FROM Track t WHERE t.album.id = :albumId")
    List<Track> findByAlbumId(Long albumId);

    @Query("SELECT t FROM Track t WHERE t.komponist.name = :komponist")
    List<Track> findByKomponist(String komponist);

    @Query("SELECT t FROM Track t WHERE t.komponist.id = :komponistId")
    List<Track> findByKomponistId(Long komponistId);

    @Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    List<Track> findByGenreLike(String genre);

    @Query("select t from Track t join fetch t.genres g where g.id = :genreId")
    List<Track> findByGenreId(Long genreId);

    List<Track> findByGenres(Genre genre);

    List<Track> findByGenresIsIn(Set<Genre> genres);

    @Query("select t from Track t join fetch t.interpreten i where i.name ilike %:interpret%")
    List<Track> findByInterpretenLike(String interpret);

    @Query("select t from Track t join fetch t.interpreten i where i.id = :interpretId")
    List<Track> findByInterpretId(Long interpretId);

    List<Track> findByInterpreten(Interpret interpret);

    List<Track> findByInterpretenIsIn(Set<Interpret> interpreten);

    @Query("SELECT t FROM Track t WHERE t.werk.name ILIKE %:werk%")
    List<Track> findByWerkLike(String werk);

    @Query("SELECT t FROM Track t WHERE t.werk.id = :werkId")
    List<Track> findByWerkId(Long werkId);
}
