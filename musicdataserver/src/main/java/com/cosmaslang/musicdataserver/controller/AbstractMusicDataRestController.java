package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackDependentRepository;
import com.cosmaslang.musicdataserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.logging.Logger;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@NoRepositoryBean
//CORS
@CrossOrigin(originPatterns = ALL)
public abstract class AbstractMusicDataRestController<ENTITY extends NamedEntity> {
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    TrackRepository trackRepository;
    @Autowired
    TrackDependentRepository<Album> albumRepository;
    @Autowired
    TrackDependentRepository<Composer> composerRepository;
    @Autowired
    TrackDependentRepository<Work> workRepository;
    @Autowired
    TrackDependentRepository<Genre> genreRepository;
    @Autowired
    TrackDependentRepository<Artist> artistRepository;
    @Autowired
    MusicDataServerConfiguration musicDataServerConfiguration;

    protected abstract NamedEntityRepository<ENTITY> getMyRepository();

    @RequestMapping(value = "/find", method = {RequestMethod.GET, RequestMethod.POST})
    protected Page<ENTITY> find(
            @Nullable Integer pageNumber,
            @Nullable Integer pageSize,
            @Nullable String name) {
        logger.info(String.format("find name=%s, page=%d pageSize=%d", name, pageNumber, pageSize));

        Pageable pageable = getPageableOf(pageNumber, pageSize);
        long time = System.currentTimeMillis();

        Page<ENTITY> page = (name == null || name.isEmpty())
                ? getMyRepository().findAll(pageable)
                : getMyRepository().findByNameContainsIgnoreCaseOrderByName(name, pageable);

        logger.info(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
        return page;
    }

    @RequestMapping(value = "/findby", method = {RequestMethod.GET, RequestMethod.POST})
    protected abstract Page<ENTITY> findBy(
            Integer pageNumber,
            Integer pageSize,
            String track, String album, String composer,
            String work, String genre, String artist);

    protected void logCall(Integer pageNumber, Integer pageSize, String track, String album, String composer,
                           String work, String genre, String artist) {
        logger.info(String.format("findby track=%s, album=%s, composer=%s, work=%s, genre=%s, artist=%s, page=%d pageSize=%d",
                track, album, composer, work, genre, artist, pageNumber, pageSize));
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    protected abstract Page<ENTITY> get(
            Integer pageNumber,
            Integer pageSize,
            Long trackId, Long albumId, Long composerId,
            Long workId, Long genreId, Long artistId);

    protected void logCall(Integer pageNumber, Integer pageSize, Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logger.info(String.format("get trackId=%d, albumId=%d, composerId=%d, workId=%d, genreId=%d, artistId=%d, page=%d pageSize=%d",
                trackId, albumId, composerId, workId, genreId, artistId, pageNumber, pageSize));
    }

    /**
     * Default getter nach id
     *
     * @param id ID der Entity. Falls fehlende => alle suchen
     */
    protected Page<ENTITY> get(Long id, NamedEntityRepository<ENTITY> entityRepository, Pageable pageable) {
        if (id != null) {
            return entityRepository.findById(id, pageable);
        }
        return getAll(entityRepository, pageable);
    }

    protected Page<ENTITY> getAll(NamedEntityRepository<ENTITY> entityRepository, Pageable pageable) {
        return entityRepository.findAll(pageable);
    }

    @GetMapping("/id/{id}")
    public ENTITY getById(@PathVariable Long id) {
        Optional<ENTITY> entity = getMyRepository().findById(id);
        if (entity.isPresent()) {
            return entity.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No item found with id " + id);
    }

    @Transactional
    @DeleteMapping("/remove/{id}")
    protected ENTITY remove(Long id, NamedEntityRepository<ENTITY> repository) {
        Optional<ENTITY> entity = repository.findById(id);
        //TODO was passiert mit Referenzen in artists_tracks usw.?
        if (entity.isPresent()) {
            repository.delete(entity.get());
            return entity.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No item found with id " + id);
    }

    protected Pageable getPageableOf(Integer pageNumber, Integer pageSize) {
        return PageRequest.of(pageNumber == null ? 0 : pageNumber,
                pageSize == null ? musicDataServerConfiguration.getPagesize() : pageSize);
    }
}
