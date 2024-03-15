package com.cosmaslang.musikserver.db.repositories;

import java.io.File;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cosmaslang.musikserver.db.entities.Album;
import com.cosmaslang.musikserver.db.entities.Track;

@Repository
public interface TrackRepository extends CrudRepository<Track, Long> {
	List<Track> findByTitle(String title);
	Track findByPath(String path);
	List<Track> findByAlbum(Album album);
	/*
	List<Track> findByKomponist(String komponist);
	List<Track> findByGenre(String genre);
	List<Track> findByWerk(String werk);
	 */
}
