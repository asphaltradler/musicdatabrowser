package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Artist;
import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
@RequestMapping("/music/artist")
public class ArtistRestController extends AbstractMusicDataRestController<Artist> {
    @Override
    public Artist getById(@PathVariable Long id) {
        return getEntityIfExists(id, artistRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected Stream<Artist> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.streamByAlbumNameContainsIgnoreCaseOrderByAlbumName(album));
        } else if (composer != null) {
            return getMappedTracks(trackRepository.streamByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer));
        } else if (work != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(work));
        } else if (genre != null) {
            return getMappedTracks(trackRepository.streamDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre));
        } else if (artist != null) {
            return artistRepository.streamByNameContainsIgnoreCaseOrderByName(artist);
        }
        return getAll(artistRepository);
    }

    @Override
    public Stream<Artist> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId).stream());
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.streamByAlbumId(albumId));
        } else if (composerId != null) {
            return getMappedTracks(trackRepository.streamByComposerId(composerId));
        } else if (workId != null) {
            return getMappedTracks(trackRepository.streamByWorkId(workId));
        } else if (genreId != null) {
            return getMappedTracks(trackRepository.streamByGenresId(genreId));
        } else if (artistId != null) {
            return getEntitiesIfExists(artistId, artistRepository);
        }
        return getAll(artistRepository);
    }

    private Stream<Artist> getMappedTracks(Stream<Track> tracks) {
        return getMappedByEntitySet(tracks, Track::getArtists);
    }
}
