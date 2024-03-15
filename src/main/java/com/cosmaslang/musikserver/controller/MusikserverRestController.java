package com.cosmaslang.musikserver.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.cosmaslang.musikserver.db.repositories.AlbumRepository;
import com.cosmaslang.musikserver.db.repositories.GenreRepository;
import com.cosmaslang.musikserver.db.repositories.KomponistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cosmaslang.musikserver.db.repositories.TrackRepository;
import com.cosmaslang.musikserver.db.entities.Track;

@RestController
@RequestMapping("/musik")
public class MusikserverRestController {
	
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	TrackRepository trackRepository;
	@Autowired
	AlbumRepository albumRepository;
	@Autowired
	KomponistRepository komponistRepository;
	@Autowired
	GenreRepository genreRepository;

	@RequestMapping(value = "/track/get", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Track> getTracks(@RequestParam(required = false) String title,
			@RequestParam(required = false) String album,
			@RequestParam(required = false) Long id) {
		List<Track> tracks;
		if (StringUtils.hasLength(title)) {
			tracks = trackRepository.findByTitle(title);
		/*} else if (album != null) {
			tracks = trackRepository.findByAlbum(album);*/
		} else if (id != null) {
			tracks = Collections.singletonList(trackRepository.findById(id).orElse(null));
		} else {
			Iterator<Track> allTracks = trackRepository.findAll().iterator();
			tracks = new ArrayList<Track>();
			allTracks.forEachRemaining(tracks::add);
		}
		return tracks;
	}
	
	@GetMapping("/track/id/{id}")
	public Track findById(@PathVariable long id) {
		Optional<Track> track = trackRepository.findById(id);
		if (track.isPresent()) {
			return track.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Track found with id " + id);
	}
	
	@PostMapping("/track/create")
	public String createTrack(@RequestBody Track track) {
		return add(track);
	}

	private String add(Track track) {
		String name = track.getTitle();
		List<Track> tracks = trackRepository.findByTitle(name);
		if (!tracks.isEmpty()) {
			track = tracks.get(0);
			return "track " + track + " already exists";
		} else {
			trackRepository.save(track);
			logger.info("added track " + track);
			return "added track " + track;
		}
	}
	
	@DeleteMapping("/track/remove")
	public String removeTrack(@RequestParam(required = true) Long id) {
		Optional<Track> track = trackRepository.findById(id);
		if (track.isPresent()) {
			trackRepository.delete(track.get());
			return track + " removed";
		}
		return "Track " + id + " not found!";
	}

	/*
	@RequestMapping(value = "/album/get", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Track> getAlbumBy(@RequestParam(required = false) String komponist,
								 @RequestParam(required = false) String werk,
								 @RequestParam(required = false) String genre) {
		List<Album> alben;
		if (StringUtils.hasLength(komponist)) {
			Komponist k = komponistRepository.findByName(komponist);
			trackRepository
			alben = komponistRepository.findByName(;
		} else if (album != null) {
			tracks = trackRepository.findByAlbum(album);
		} else if (id != null) {
			tracks = Collections.singletonList(trackRepository.findById(id).orElse(null));
		} else {
			Iterator<Track> allTracks = trackRepository.findAll().iterator();
			tracks = new ArrayList<Track>();
			allTracks.forEachRemaining(tracks::add);
		}
		return tracks;
	}
*/
}
