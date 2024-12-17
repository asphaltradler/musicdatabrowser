package com.cosmaslang.musicdataserver.db.repositories;

import com.cosmaslang.musicdataserver.db.entities.Album;
import com.cosmaslang.musicdataserver.db.entities.Track;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

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
class AlbumRepositoryTest {
    public static final String ALBUM_1 = "Album 1";
    public static final String TRACK_1 = "Track 1";
    public static final String TRACK_2 = "Track 2";

    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    TrackRepository trackRepository;

    Pageable pageable = Pageable.unpaged();
    Album album;
    Track track1;
    Track track2;

    @BeforeAll
    static void setUp(@Autowired AlbumRepository albumRepository, @Autowired TrackRepository trackRepository) {
        Album album;
        if ((album = albumRepository.findByName(ALBUM_1)) == null) {
            album = new Album();
            album.setName(ALBUM_1);
            album = albumRepository.save(album);
        }

        Set<Track> tracks = new HashSet<>();
        addTrack(TRACK_1, tracks, album);
        addTrack(TRACK_2, tracks, album);
        trackRepository.saveAll(tracks);
        //mir nicht ganz klar, wieso ich das machen muss? Im Service nicht notwendig
        album.setTracks(tracks);
    }

    @BeforeEach
    void setUp() {
        album = albumRepository.findByName(ALBUM_1);
        track1 = trackRepository.findByName(TRACK_1);
        track2 = trackRepository.findByName(TRACK_2);
    }

    private static void addTrack(String name, Set<Track> tracks, Album album) {
        Track track = new Track();
        track.setName(name);
        track.setAlbum(album);
        track.setPath("path/"+name);
        track.setSize(1234567L);
        createHash(track);
        tracks.add(track);
    }

    private static void createHash(Track track) {
        track.setHash(Long.toHexString(Objects.hash(track.getName(), Math.random())));
    }

    @Test
    void findAlbumByName() {
        List<Album> foundAlbums = albumRepository.findByNameContainsIgnoreCaseOrderByName(ALBUM_1, pageable).stream().toList();
        assertEquals(1, foundAlbums.size());
        assertEquals(foundAlbums.getFirst(), album);
    }

    @Test
    void findTracks() {
        assertEquals(track1, trackRepository.findByName(TRACK_1));
        assertEquals(track2, trackRepository.findByName(TRACK_2));
    }

    @Test
    //wichtig für lazy initialization
    @Transactional
    void findTracksInAlbum() {
        Album album = albumRepository.findByName(ALBUM_1);
        assertNotNull(album);

        Set<Track> albumTracks = album.getTracks();
        assertNotNull(albumTracks);
        assertEquals(2, albumTracks.size());
    }

    @Test
    void findTracksForAlbum() {
        List<Track> albumTracks = trackRepository.findByAlbumNameContainsIgnoreCaseOrderByAlbumName(ALBUM_1, pageable).stream().toList();
        assertEquals(2, albumTracks.size());
        assertTrue(albumTracks.contains(trackRepository.findByName(TRACK_1)));
        assertTrue(albumTracks.contains(trackRepository.findByName(TRACK_2)));
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