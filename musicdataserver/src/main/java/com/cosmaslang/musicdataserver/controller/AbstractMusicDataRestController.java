package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.AlbumRepository;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@NoRepositoryBean
//CORS
@CrossOrigin(originPatterns = ALL)
public abstract class AbstractMusicDataRestController<ENTITY extends NamedEntity> {
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    TrackRepository trackRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    NamedEntityRepository<Composer> composerRepository;
    @Autowired
    NamedEntityRepository<Work> workRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;
    @Autowired
    NamedEntityRepository<Artist> artistRepository;

    @GetMapping("/id/{id}")
    protected abstract ENTITY getById(@PathVariable Long id);

    @DeleteMapping("/remove")
    protected abstract String remove(@RequestParam() Long id);

    @RequestMapping(value = "/find", method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional(readOnly = true)
    protected abstract List<ENTITY> find(@RequestParam(required = false) String track,
                                @RequestParam(required = false) String album,
                                @RequestParam(required = false) String composer,
                                @RequestParam(required = false) String work,
                                @RequestParam(required = false) String genre,
                                @RequestParam(required = false) String artist);

    protected void logCall(String track, String album, String composer, String work, String genre, String artist) {
        logger.info(MessageFormat.format("{0} find track={1}, album={2}, composer={3}, work={4}, genre={5}, artist={6}", this.getClass().getName(),
                track, album, composer, work, genre, artist));
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional(readOnly = true)
    protected abstract List<ENTITY> get(@RequestParam(required = false) Long trackId,
                               @RequestParam(required = false) Long albumId,
                               @RequestParam(required = false) Long composerId,
                               @RequestParam(required = false) Long workId,
                               @RequestParam(required = false) Long genreId,
                               @RequestParam(required = false) Long artistId);

    protected void logCall(Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logger.info(MessageFormat.format("{0} get trackId={1}, albumId={2}, composerId={3}, workId={4}, genreId={5}, artistId={6}", this.getClass().getName(),
                trackId, albumId, composerId, workId, genreId, artistId));
    }

    /**
     * Default getter nach id
     *
     * @param id ID der Entity. Falls fehlende => alle suchen
     */
    protected List<ENTITY> get(Long id, NamedEntityRepository<ENTITY> entityRepository) {
        if (id != null) {
            return getEntitiesIfExists(id, entityRepository);
        }
        return getAll(entityRepository);
    }

    protected List<ENTITY> getEntitiesIfExists(Long id, NamedEntityRepository<ENTITY> repository) {
        Optional<ENTITY> entity = repository.findById(id);
        return entity.map(Collections::singletonList).orElse(Collections.emptyList());
    }

    protected ENTITY getEntityIfExists(Long id, NamedEntityRepository<ENTITY> repository) {
        Optional<ENTITY> entity = repository.findById(id);
        if (entity.isPresent()) {
            return entity.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No item found with id " + id);
    }

    protected List<ENTITY> getAll(NamedEntityRepository<ENTITY> repository) {
        Iterator<ENTITY> entityIt = repository.findAll().iterator();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(entityIt, 0), false).toList();
    }

    protected List<ENTITY> getMappedByEntitySet(List<Track> tracks,
                                                Function<Track, Set<ENTITY>> mapFunction) {
        return tracks.stream().map(mapFunction).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
    }

    protected List<ENTITY> getMappedByEntity(List<Track> tracks,
                                             Function<Track, ENTITY> mapFunction) {
        return tracks.stream().map(mapFunction).filter(Objects::nonNull).distinct().sorted().toList();
    }
}
