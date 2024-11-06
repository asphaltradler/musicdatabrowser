package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.*;
import com.cosmaslang.musikdataserver.db.repositories.AlbumRepository;
import com.cosmaslang.musikdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikdataserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

@NoRepositoryBean
//CORS
@CrossOrigin(originPatterns = "http://localhost:42*")
public abstract class AbstractMusikRestController<ENTITY extends NamedEntity> {
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    TrackRepository trackRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    NamedEntityRepository<Komponist> komponistRepository;
    @Autowired
    NamedEntityRepository<Werk> werkRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;
    @Autowired
    NamedEntityRepository<Interpret> interpretRepository;

    @GetMapping("/id/{id}")
    protected abstract ENTITY getById(@PathVariable Long id);

    @DeleteMapping("/remove")
    protected abstract String remove(@RequestParam() Long id);

    @RequestMapping(value = "/find", method = {RequestMethod.GET, RequestMethod.POST})
    protected List<ENTITY> find(@RequestParam(required = false) String track,
                                @RequestParam(required = false) String album,
                                @RequestParam(required = false) String komponist,
                                @RequestParam(required = false) String werk,
                                @RequestParam(required = false) String genre,
                                @RequestParam(required = false) String interpret) {
        logger.info(MessageFormat.format("{0} find track={1}, album={2}, komponist={3}, werk={4}, genre={5}, interpret={6}", this.getClass().getName(),
                track, album, komponist, werk, genre, interpret));
        return null;
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    protected List<ENTITY> get(@RequestParam(required = false) Long trackId,
                               @RequestParam(required = false) Long albumId,
                               @RequestParam(required = false) Long komponistId,
                               @RequestParam(required = false) Long werkId,
                               @RequestParam(required = false) Long genreId,
                               @RequestParam(required = false) Long interpretId) {
        logger.info(MessageFormat.format("{0} get trackId={1}, albumId={2}, komponistId={3}, werkId={4}, genreId={5}, interpretId={6}", this.getClass().getName(),
                trackId, albumId, komponistId, werkId, genreId, interpretId));
        return null;
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
        List<ENTITY> entities = new ArrayList<>();
        entityIt.forEachRemaining(entities::add);
        return entities.stream().sorted().toList();
    }

    protected List<ENTITY> getFilteredByEntitySet(List<Track> tracks,
                                                  Function<Track, Set<ENTITY>> mapFunction) {
        return tracks.stream().map(mapFunction).flatMap(Collection::stream).filter(Objects::nonNull).distinct().sorted().toList();
    }

    protected List<ENTITY> getFilteredByEntity(List<Track> tracks,
                                               Function<Track, ENTITY> mapFunction) {
        return tracks.stream().map(mapFunction).filter(Objects::nonNull).distinct().sorted().toList();
    }
}
