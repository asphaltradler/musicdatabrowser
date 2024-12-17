package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Album;
import com.cosmaslang.musicdataserver.db.entities.Track;
import com.cosmaslang.musicdataserver.db.repositories.AlbumRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
class AlbumRestControllerTest {
    public static final String ALBUM_1 = "Album 1";
    public static final String TRACK_1 = "Track 1";
    public static final String TRACK_2 = "Track 2";

    @Autowired
    AlbumRestController albumRestController;
    @Autowired
    TrackRestController trackRestController;

    Pageable pageable = Pageable.unpaged();
    Album album;
    Track track1;
    Track track2;

    @BeforeEach
    public void setUp() {
        if ((album = albumRestController.getMyRepository().findByName(ALBUM_1)) == null) {
            album = new Album();
            album.setName(ALBUM_1);
        }
        album = albumRestController.getMyRepository().save(album);

        Set<Track> tracks = new HashSet<>();

        if ((track1 = trackRestController.getMyRepository().findByName(TRACK_1)) == null) {
            track1 = createTrack(TRACK_1, album);
        }
        tracks.add(track1);
        if ((track2 = trackRestController.getMyRepository().findByName(TRACK_2)) == null) {
            track2 = createTrack(TRACK_2, album);
        }
        tracks.add(track2);
        //mir nicht ganz klar, wieso ich das machen muss? Im Service nicht notwendig
        //album.setTracks(tracks);
        track1 = trackRestController.getMyRepository().save(track1);
        track2 = trackRestController.getMyRepository().save(track2);
    }

    private static Track createTrack(String name, Album album) {
        Track track = new Track();
        track.setName(name);
        track.setAlbum(album);
        track.setPath("path/" + name);
        track.setSize(1234567L);
        createHash(track);
        return track;
    }

    private static void createHash(Track track) {
        track.setHash(Long.toHexString(Objects.hash(track.getName(), Math.random())));
    }

    @Test
    void findAllAlbumsFromController() {
        List<Album> allAlbums = albumRestController.getAll(pageable).stream().toList();
        assertTrue(allAlbums.contains(album));
        assertEquals(albumRestController.getMyRepository().count(), allAlbums.size());
    }

    @Test
    void findAllTracksFromController() {
        List<Track> allTracks = trackRestController.getAll(pageable).stream().toList();
        assertEquals(trackRestController.getMyRepository().count(), allTracks.size());
        assertTrue(allTracks.contains(track1));
        assertTrue(allTracks.contains(track2));
        assertEquals(album, track1.getAlbum());
        assertEquals(album, track2.getAlbum());
    }

    @Test
        //@Disabled("Verdrahtung zu Controller und seinem Repository funktioniert nicht")
    void findTracksForAlbumFromController() {
        List<Track> albumTracks = trackRestController.findBy(0, 10, null, ALBUM_1, null, null, null, null).stream().toList();
        assertEquals(2, albumTracks.size());
        assertTrue(albumTracks.contains(track1));
        assertTrue(albumTracks.contains(track2));
    }

    @Test
    void deleteAlbumShouldRemoveAllTracksInAlbum() {
        long trackCount = trackRestController.getMyRepository().count();

        albumRestController.remove(album.getId());

        Album findAlbum = albumRestController.getMyRepository().findByName(ALBUM_1);
        assertNull(findAlbum);
        List<Album> foundAlbums = albumRestController.getAll(pageable).stream().toList();
        assertFalse(foundAlbums.contains(album));

        assertEquals(trackCount - 2, trackRestController.getMyRepository().count());
        List<Track> foundTracks = trackRestController.getAll(pageable).stream().toList();
        assertFalse(foundTracks.contains(track1));
        assertFalse(foundTracks.contains(track2));
    }

    @AfterAll
    public static void tearDown(@Autowired AlbumRepository albumRepository, @Autowired TrackRepository trackRepository) {
        albumRepository.delete(albumRepository.findByName(ALBUM_1));
        //trackRepository.delete(trackRepository.findByName(TRACK_1));
        //trackRepository.delete(trackRepository.findByName(TRACK_2));
    }
}