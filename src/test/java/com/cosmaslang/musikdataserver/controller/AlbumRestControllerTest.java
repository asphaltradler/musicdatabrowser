package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Album;
import com.cosmaslang.musikdataserver.db.entities.Track;
import com.cosmaslang.musikdataserver.db.repositories.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AlbumRestControllerTest {

    public static final String ALBUM_1 = "Album 1";
    public static final String TRACK_1 = "Track 1";
    public static final String TRACK_2 = "Track 2";

    AlbumRestController albumRestController;
    AlbumRepository albumRepository;
    Album album;

    @BeforeEach
    void setUp() {
        albumRestController = new AlbumRestController();
        albumRepository = albumRestController.albumRepository;

        Set<Track> tracks = new HashSet<>();
        Track track1 = new Track();
        track1.setName(TRACK_1);
        Track track2 = new Track();
        track2.setName(TRACK_2);
        tracks.add(track1);
        tracks.add(track2);

        album = new Album();
        album.setName(ALBUM_1);
        album.setTracks(tracks);

        albumRepository.save(album);
    }

    @Test
    void getAllAlbums() {
        List<Album> foundAlbums = albumRestController.getAll(albumRepository);
        assertEquals(1, foundAlbums.size());
        assertEquals(foundAlbums.get(0), album);
    }

    @Test
    void findAlbumByName() {
        List<Album> foundAlbums = albumRepository.findByNameContainingIgnoreCase(ALBUM_1);
        assertEquals(1, foundAlbums.size());
        assertEquals(foundAlbums.get(0), album);
    }

    @Test
    void getTracksInAlbum() {
        Album album = albumRepository.findByName(ALBUM_1);
        assertNotNull(album);

        Set<Track> albumTracks = album.getTracks();
        assertEquals(2, albumTracks.size());
    }

    @Test
    void deleteAlbumShouldRemoveAllTracksInAlbum() {
        List<Album> foundAlbums = albumRepository.findByNameContainingIgnoreCase(ALBUM_1);
        assertEquals(1, foundAlbums.size());

        TrackRestController trackRestController = new TrackRestController();
        List<Track> allTracks = trackRestController.getAll(trackRestController.trackRepository);
        assertEquals(2, allTracks.size());

        albumRepository.delete(foundAlbums.get(0));

        foundAlbums = albumRestController.getAll(albumRepository);
        assertEquals(0, foundAlbums.size());
        allTracks = trackRestController.getAll(trackRestController.trackRepository);
        assertEquals(0, allTracks.size());
    }

}