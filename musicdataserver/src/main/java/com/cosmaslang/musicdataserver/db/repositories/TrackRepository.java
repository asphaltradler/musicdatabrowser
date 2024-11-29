package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface TrackRepository extends NamedEntityRepository<Track> {
    Track findByPath(String path);
    Track findByHash(String hash);

    //@Query("SELECT t FROM Track t WHERE t.album.name ilike %:album%")
        //identisch im Verhalten mit:
        //@Query("SELECT a.tracks FROM Album a WHERE a.name like %:album%")
    Stream<Track> streamByAlbumNameContainsIgnoreCaseOrderByAlbumName(String album);
    //@Query("SELECT t FROM Track t WHERE t.composer.name = :composer")
    Stream<Track> streamByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(String composer);
    @Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    Stream<Track> streamDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(String genre);
    @Query("select t from Track t join fetch t.artists i where i.name ilike %:artist%")
    Stream<Track> streamDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(String artist);
    //@Query("SELECT t FROM Track t WHERE t.work.name ILIKE %:work%")
    Stream<Track> streamByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscId(String work);

    Stream<Track> streamByAlbumId(Long id);
    Stream<Track> streamByComposerId(Long id);
    Stream<Track> streamByWorkId(Long id);
    Stream<Track> streamByArtistsId(Long id);
    //@Query("select t from Track t join fetch t.genres g where g.id = :genreId")
    Stream<Track> streamByGenresId(Long id);
}
