package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends NamedEntityRepository<Track> {
    @Override
    default String getName() { return "Track"; }

    Track findByPath(String path);
    Track findByHash(String hash);

    //@Query("SELECT t FROM Track t WHERE t.album.name ilike %:album%")
        //identisch im Verhalten mit:
        //@Query("SELECT a.tracks FROM Album a WHERE a.name like %:album%")
    Page<Track> findByAlbumNameContainsIgnoreCaseOrderByAlbumNameAscTracknumber(String album, Pageable pageable);
    //@Query("SELECT t FROM Track t WHERE t.composer.name = :composer")
    Page<Track> findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscTracknumber(String composer, Pageable pageable);
    @Query("select t from Track t join fetch t.genres g where g.name ilike %:genre%")
    Page<Track> findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscTracknumber(String genre, Pageable pageable);
    @Query("select t from Track t join fetch t.artists i where i.name ilike %:artist%")
    Page<Track> findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscTracknumber(String artist, Pageable pageable);
    //@Query("SELECT t FROM Track t WHERE t.work.name ILIKE %:work%")
    Page<Track> findByWorkNameContainsIgnoreCaseOrderByWorkNameAscAlbumNameAscTracknumber(String work, Pageable pageable);

    Page<Track> findByAlbumIdOrderByAlbumNameAscTracknumber(Long id, Pageable pageable);
    Page<Track> findByComposerIdOrderByAlbumNameAscTracknumber(Long id, Pageable pageable);
    Page<Track> findByWorkIdOrderByAlbumNameAscTracknumber(Long id, Pageable pageable);
    Page<Track> findByArtistsIdOrderByAlbumNameAscTracknumber(Long id, Pageable pageable);
    //@Query("select t from Track t join fetch t.genres g where g.id = :genreId")
    Page<Track> findByGenresIdOrderByAlbumNameAscTracknumber(Long id, Pageable pageable);
}
