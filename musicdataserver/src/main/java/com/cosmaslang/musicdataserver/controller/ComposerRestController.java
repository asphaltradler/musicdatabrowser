package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Composer;
import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
@RequestMapping("/music/composer")
public class ComposerRestController extends AbstractMusicDataRestController<Composer> {
    @Override
    public Composer getById(@PathVariable Long id) {
        return getEntityIfExists(id, composerRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected Stream<Composer> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.streamByAlbumNameContainsIgnoreCaseOrderByAlbumName(album));
        } else if (composer != null) {
            return composerRepository.streamByNameContainsIgnoreCaseOrderByName(composer);
        } else if (work != null) {
            return getMappedTracks(trackRepository.streamByNameContainsIgnoreCaseOrderByName(work));
        } else if (genre != null) {
            return getMappedTracks(trackRepository.streamDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre));
        } else if (artist != null) {
            return getMappedTracks(trackRepository.streamDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist));
        }
        return getAll(composerRepository);
    }

    @Override
    public Stream<Composer> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId).stream());
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.streamByAlbumId(albumId));
        } else if (composerId != null) {
            return composerRepository.findById(composerId).stream();
        } else if (workId != null) {
            return getMappedTracks(trackRepository.streamByWorkId(workId));
        } else if (genreId != null) {
            return getMappedTracks(trackRepository.streamByGenresId(genreId));
        } else if (artistId != null) {
            return getMappedTracks(trackRepository.streamByArtistsId(artistId));
        }
        return getAll(composerRepository);
    }

    private Stream<Composer> getMappedTracks(Stream<Track> tracks) {
        return getMappedByEntity(tracks, Track::getComposer);
    }
}
