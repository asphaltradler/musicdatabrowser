package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Album;
import com.cosmaslang.musikdataserver.db.entities.Track;
import com.cosmaslang.musikdataserver.db.repositories.AlbumRepository;
import com.cosmaslang.musikdataserver.db.repositories.TrackRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumRestControllerTest {

    public static final String ALBUM_1 = "Album 1";
    public static final String TRACK_1 = "Track 1";
    public static final String TRACK_2 = "Track 2";

    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    TrackRepository trackRepository;
    AlbumRestController albumRestController;
    TrackRestController trackRestController;
    Album album;
    Track track1;
    Track track2;

    @BeforeEach
    void setUp() {
        albumRestController = new AlbumRestController();
        trackRestController = new TrackRestController();

        if ((album = albumRepository.findByName(ALBUM_1)) == null) {
            album = new Album();
            album.setName(ALBUM_1);
            album = albumRepository.save(album);
        }

        Set<Track> tracks = new HashSet<>();
        track1 = addTrack(TRACK_1, tracks);
        track2 = addTrack(TRACK_2, tracks);
        trackRepository.saveAll(tracks);
        //mir nicht ganz klar, wieso ich das machen muss? Im Service nicht notwendig
        album.setTracks(tracks);
    }

    private Track addTrack(String name, Set<Track> tracks) {
        Track track = new Track();
        track.setName(name);
        track.setAlbum(album);
        createHash(track);
        tracks.add(track);
        return track;
    }

    private void createHash(Track track) {
        track.setHash(Long.toHexString(Objects.hashCode(track.getName())));
    }

    @Test
    void getAllAlbumsFromController() {
        List<Album> allAlbums = albumRestController.getAll(albumRepository);
        assertTrue(allAlbums.contains(album));
        assertEquals(albumRepository.count(), allAlbums.size());
    }

    @Test
    void findAlbumByName() {
        List<Album> foundAlbums = albumRepository.findByNameContainingIgnoreCase(ALBUM_1);
        assertEquals(1, foundAlbums.size());
        assertEquals(foundAlbums.get(0), album);
    }

    @Test
    void getAllTracksFromController() {
        List<Track> allTracks = trackRestController.getAll(trackRepository);
        assertEquals(trackRepository.count(), allTracks.size());
        assertTrue(allTracks.contains(track1));
        assertTrue(allTracks.contains(track2));
        assertEquals(album, track1.getAlbum());
        assertEquals(album, track2.getAlbum());
    }

    @Test
    void findTracks() {
        assertEquals(track1, trackRepository.findByName(TRACK_1));
        assertEquals(track2, trackRepository.findByName(TRACK_2));
    }

    @Test
    void getTracksInAlbum() {
        Album album = albumRepository.findByName(ALBUM_1);
        assertNotNull(album);

        Set<Track> albumTracks = album.getTracks();
        assertNotNull(albumTracks);
        assertEquals(2, albumTracks.size());
    }

    @Test
    void getTracksForAlbum() {
        List<Track> albumTracks = trackRepository.findByAlbumLike(ALBUM_1);
        assertEquals(2, albumTracks.size());
        assertTrue(albumTracks.contains(trackRepository.findByName(TRACK_1)));
        assertTrue(albumTracks.contains(trackRepository.findByName(TRACK_2)));
    }

    @Test
    @Disabled("Verdrahtung zu Controller und seinem Repository funktioniert nicht")
    void getTracksForAlbumFromController() {
        List<Track> albumTracks = trackRestController.get(null, ALBUM_1, null, null, null, null, null);
        assertEquals(2, albumTracks.size());
        assertTrue(albumTracks.contains(track1));
        assertTrue(albumTracks.contains(track2));
    }

    @Test
    void deleteAlbumShouldRemoveAllTracksInAlbum() {
        long trackCount = trackRepository.count();

        albumRepository.delete(album);

        Album findAlbum = albumRepository.findByName(ALBUM_1);
        assertNull(findAlbum);
        List<Album> foundAlbums = albumRestController.getAll(albumRepository);
        assertFalse(foundAlbums.contains(album));

        assertEquals(trackCount-2, trackRepository.count());
        List<Track> foundTracks = trackRestController.getAll(trackRepository);
        assertFalse(foundTracks.contains(track1));
        assertFalse(foundTracks.contains(track2));
    }

    @AfterAll
    public static void tearDown(@Autowired AlbumRepository albumRepository, @Autowired TrackRepository trackRepository) {
        Track track1 = trackRepository.findByName(TRACK_1);
        Track track2 = trackRepository.findByName(TRACK_2);
        if (track1 != null) {
            trackRepository.delete(track1);
        }
        if (track2 != null) {
            trackRepository.delete(track2);
        }
        Album album = albumRepository.findByName(ALBUM_1);
        if (album != null) {
            albumRepository.delete(album);
        }
    }

}