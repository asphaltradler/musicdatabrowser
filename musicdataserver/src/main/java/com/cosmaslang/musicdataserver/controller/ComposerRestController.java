package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Composer;
import com.cosmaslang.musicdataserver.db.entities.Track;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    protected List<Composer> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            return getMappedTracks(trackRepository.findByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(album));
        } else if (composer != null) {
            return composerRepository.findByNameContainsIgnoreCaseOrderByName(composer);
        } else if (work != null) {
            return getMappedTracks(trackRepository.findByNameContainsIgnoreCaseOrderByName(work));
        } else if (genre != null) {
            return getMappedTracks(trackRepository.findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre));
        } else if (artist != null) {
            return getMappedTracks(trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist));
        }
        return getAll(composerRepository);
    }

    @Override
    public List<Composer> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId).stream().toList());
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.findByAlbumId(albumId));
        } else if (composerId != null) {
            return composerRepository.findById(composerId).stream().toList();
        } else if (workId != null) {
            return getMappedTracks(trackRepository.findByWorkId(workId));
        } else if (genreId != null) {
            return getMappedTracks(trackRepository.findByGenresId(genreId));
        } else if (artistId != null) {
            return getMappedTracks(trackRepository.findByArtistsId(artistId));
        }
        return getAll(composerRepository);
    }

    private List<Composer> getMappedTracks(List<Track> tracks) {
        return getMappedByEntity(tracks, Track::getComposer);
    }
}
