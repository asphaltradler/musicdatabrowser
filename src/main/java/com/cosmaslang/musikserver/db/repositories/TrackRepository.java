package com.cosmaslang.musikserver.db.repositories;

import com.cosmaslang.musikserver.db.entities.Album;
import com.cosmaslang.musikserver.db.entities.Track;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
