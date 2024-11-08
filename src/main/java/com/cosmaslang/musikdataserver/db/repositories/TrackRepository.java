package com.cosmaslang.musikdataserver.db.repositories;

import com.cosmaslang.musikdataserver.db.entities.Track;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface TrackRepository extends NamedEntityRepository<Track> {
    Track findByPath(String path);

    //@Query("SELECT t FROM Track t WHERE t.album.name ilike %:album%")
        //identisch im Verhalten mit:
        //@Query("SELECT a.tracks FROM Album a WHERE a.name like %:album%")
    Stream<Track> streamByAlbum_NameContainsIgnoreCase(String album);
    //@Query("SELECT t FROM Track t WHERE t.komponist.name = :komponist")
    Stream<Track> streamByKomponist_Name(String komponist);
    //@Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    Stream<Track> streamByGenres_NameContainsIgnoreCase(String genre);
    //@Query("select t from Track t join fetch t.interpreten i where i.name ilike %:interpret%")
    Stream<Track> streamByInterpreten_NameContainsIgnoreCase(String interpret);
    //@Query("SELECT t FROM Track t WHERE t.werk.name ILIKE %:werk%")
    Stream<Track> streamByWerk_NameContainsIgnoreCase(String werk);

    Stream<Track> streamByAlbum_Id(Long id);
    Stream<Track> streamByKomponist_Id(Long id);
    Stream<Track> streamByWerk_Id(Long id);
    Stream<Track> streamByInterpreten_Id(Long id);
    //@Query("select t from Track t join fetch t.genres g where g.id = :genreId")
    Stream<Track> streamByGenres_Id(Long id);
}
