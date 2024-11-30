package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends NamedEntityRepository<Track> {
    Track findByPath(String path);
    Track findByHash(String hash);

    //@Query("SELECT t FROM Track t WHERE t.album.name ilike %:album%")
        //identisch im Verhalten mit:
        //@Query("SELECT a.tracks FROM Album a WHERE a.name like %:album%")
    Page<Track> findByAlbumNameContainsIgnoreCaseOrderByAlbumName(String album, Pageable pageable);
    //@Query("SELECT t FROM Track t WHERE t.composer.name = :composer")
    Page<Track> findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(String composer, Pageable pageable);
    @Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    Page<Track> findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(String genre, Pageable pageable);
    @Query("select t from Track t join fetch t.artists i where i.name ilike %:artist%")
    Page<Track> findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(String artist, Pageable pageable);
    //@Query("SELECT t FROM Track t WHERE t.work.name ILIKE %:work%")
    Page<Track> findByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscId(String work, Pageable pageable);

    Page<Track> findByAlbumId(Long id, Pageable pageable);
    Page<Track> findByComposerId(Long id, Pageable pageable);
    Page<Track> findByWorkId(Long id, Pageable pageable);
    Page<Track> findByArtistsId(Long id, Pageable pageable);
    //@Query("select t from Track t join fetch t.genres g where g.id = :genreId")
    Page<Track> findByGenresId(Long id, Pageable pageable);
}
