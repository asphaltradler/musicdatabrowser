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
    Stream<Track> streamByAlbumNameContainsIgnoreCase(String album);
    //@Query("SELECT t FROM Track t WHERE t.komponist.name = :komponist")
    Stream<Track> streamByKomponistName(String komponist);
    //@Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    Stream<Track> streamByGenresNameContainsIgnoreCase(String genre);
    //@Query("select t from Track t join fetch t.interpreten i where i.name ilike %:interpret%")
    Stream<Track> streamByInterpretenNameContainsIgnoreCase(String interpret);
    //@Query("SELECT t FROM Track t WHERE t.werk.name ILIKE %:werk%")
    Stream<Track> streamByWerkNameContainsIgnoreCase(String werk);

    Stream<Track> streamByAlbumId(Long id);
    Stream<Track> streamByKomponistId(Long id);
    Stream<Track> streamByWerkId(Long id);
    Stream<Track> streamByInterpretenId(Long id);
    //@Query("select t from Track t join fetch t.genres g where g.id = :genreId")
    Stream<Track> streamByGenresId(Long id);
}
