package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface TrackRepository extends NamedEntityRepository<Track> {
    Track findByPath(String path);
    Track findByHash(String hash);

    //@Query("SELECT t FROM Track t WHERE t.album.name ilike %:album%")
        //identisch im Verhalten mit:
        //@Query("SELECT a.tracks FROM Album a WHERE a.name like %:album%")
    List<Track> findByAlbumNameContainsIgnoreCaseOrderByAlbumName(String album);
    //@Query("SELECT t FROM Track t WHERE t.composer.name = :composer")
    List<Track> findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(String composer);
    @Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    List<Track> findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(String genre);
    @Query("select t from Track t join fetch t.artists i where i.name ilike %:artist%")
    List<Track> findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(String artist);
    //@Query("SELECT t FROM Track t WHERE t.work.name ILIKE %:work%")
    List<Track> findByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscId(String work);

    List<Track> findByAlbumId(Long id);
    List<Track> findByComposerId(Long id);
    List<Track> findByWorkId(Long id);
    List<Track> findByArtistsId(Long id);
    //@Query("select t from Track t join fetch t.genres g where g.id = :genreId")
    List<Track> findByGenresId(Long id);
}
