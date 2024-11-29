package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Track;
import com.cosmaslang.musicdataserver.db.entities.Work;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/music/work")
public class WorkRestController extends AbstractMusicDataRestController<Work> {
    @Override
    public Work getById(@PathVariable Long id) {
        return getEntityIfExists(id, workRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected List<Work> find(String track, String album, String composer, String work, String genre, String artist) {
        super.logCall(track, album, composer, work, genre, artist);
        if (track != null) {
            return getMappedTracks(trackRepository.findByNameContainsIgnoreCaseOrderByName(track));
        } else if (album != null) {
            return getMappedTracks(trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(album));
        } else if (composer != null) {
            return getMappedTracks(trackRepository.findByComposerNameContainsIgnoreCaseOrderByComposerNameAscAlbumNameAscId(composer));
        } else if (work != null) {
            return workRepository.findByNameContainsIgnoreCaseOrderByName(work);
        } else if (genre != null) {
            return getMappedTracks(trackRepository.findDistinctByGenresNameContainsIgnoreCaseOrderByGenresNameAscAlbumNameAscId(genre));
        } else if (artist != null) {
            return getMappedTracks(trackRepository.findDistinctByArtistsNameContainsIgnoreCaseOrderByArtistsNameAscAlbumNameAscId(artist));
        }
        return getAll(workRepository);
    }

    @Override
    public List<Work> get(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        super.logCall(trackId, albumId, composerId, workId, genreId, artistId);
        if (trackId != null) {
            return getMappedTracks(trackRepository.findById(trackId).stream().toList());
        } else if (albumId != null) {
            return getMappedTracks(trackRepository.findByAlbumId(albumId));
        } else if (composerId != null) {
            return getMappedTracks(trackRepository.findByComposerId(composerId));
        } else if (workId != null) {
            return getEntitiesIfExists(workId, workRepository);
        } else if (genreId != null) {
            return getMappedTracks(trackRepository.findByGenresId(genreId));
        } else if (artistId != null) {
            return getMappedTracks(trackRepository.findByArtistsId(artistId));
        }

        return getAll(workRepository);
    }

    private List<Work> getMappedTracks(List<Track> tracks) {
        return getMappedByEntity(tracks, Track::getWork);
    }
}
