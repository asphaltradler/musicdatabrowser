package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/music/album")
public class AlbumRestController extends AbstractMusicDataRestController<Album> {
    Pageable pageable = PageRequest.ofSize(10);
    @Override
    public Stream<Album> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            Page<Album> page = albumRepository.findDistinctByTracksNameContainsIgnoreCase(track, pageable);
            logger.info("Found %d albums on %d pages".formatted(page.getTotalElements(), page.getTotalPages()));
            //pageable = pageable.next();
            return page.stream();
        } else if (album != null) {
            return albumRepository.streamByNameContainsIgnoreCaseOrderByName(album);
        } else if (composer != null) {
            return albumRepository.streamDistinctByTracksComposerNameContainsIgnoreCaseOrderByName(composer);
        } else if (work != null) {
            return albumRepository.streamDistinctByTracksWorkNameContainsIgnoreCaseOrderByName(work);
        } else if (genre != null) {
            //"von Hand" wäre:
            //Stream<Track> tracks = trackRepository.streamByGenreLike(genre);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            //über spezielle Query:
            return albumRepository.streamDistinctByTracksGenresNameContainsIgnoreCaseOrderByName(genre);
        } else if (artist != null) {
            //Stream<Track> tracks = trackRepository.streamByArtistsLike(artist);
            //return tracks.stream().map(Track::getAlbum).distinct().toList();
            return albumRepository.streamDistinctByTracksArtistsNameContainsIgnoreCaseOrderByName(artist);
        }
        return getAll(albumRepository);
    }

    @Override
    public Stream<Album> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (albumId != null) {
            return getEntitiesIfExists(albumId, albumRepository);
        } else if (trackId != null) {
            return albumRepository.streamByTracksId(trackId);
        } else if (composerId != null) {
            return albumRepository.streamDistinctByTracksComposerIdOrderByName(composerId);
        } else if (workId != null) {
            return albumRepository.streamDistinctByTracksWorkIdOrderByName(workId);
        } else if (genreId != null) {
            return albumRepository.streamDistinctByTracksGenresIdOrderByName(genreId);
        } else if (artistId != null) {
            return albumRepository.streamDistinctByTracksArtistsIdOrderByName(artistId);
        }

        return getAll(albumRepository);
    }

    @Override
    public Album getById(@PathVariable Long id) {
        return getEntityIfExists(id, albumRepository);
    }

    @Override
    public String remove(@RequestParam() Long id) {
        //zugehörige Tracks entfernen erledigt Cascadierung
        Optional<Album> album = albumRepository.findById(id);
        if (album.isPresent()) {
            albumRepository.delete(album.get());
            return album + " removed";
        }
        return "album " + id + " not found!";
    }
}
