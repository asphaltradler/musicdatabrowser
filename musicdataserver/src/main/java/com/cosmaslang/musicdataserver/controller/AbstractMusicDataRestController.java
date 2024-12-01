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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
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
    @Transactional(readOnly = true)
    protected abstract Page<ENTITY> find(
            Integer pagenumber,
            Integer pagesize,
            String track, String album, String composer,
            String work, String genre, String artist);

    protected void logCall(Integer pagenumber, Integer pagesize, String track, String album, String composer,
                           String work, String genre, String artist) {
        logger.info(MessageFormat.format("{0} find track={1}, album={2}, composer={3}, work={4}, genre={5}, artist={6} page={7} size={8}", this.getClass().getName(),
                track, album, composer, work, genre, artist, pagenumber, pagesize));
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional(readOnly = true)
    protected abstract Page<ENTITY> get(
            Integer pagenumber,
            Integer pagesize,
            Long trackId, Long albumId, Long composerId,
            Long workId, Long genreId, Long artistId);

    protected void logCall(Integer pagenumber, Integer pagesize, Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logger.info(MessageFormat.format("{0} get trackId={1}, albumId={2}, composerId={3}, workId={4}, genreId={5}, artistId={6} page={7} size={8}", this.getClass().getName(),
                trackId, albumId, composerId, workId, genreId, artistId, pagenumber, pagesize));
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

    protected Pageable getPageableOf(Integer pagenumber, Integer pagesize) {
        return PageRequest.of(pagenumber == null ? 0 : pagenumber,
                pagesize == null ? musicDataServerConfiguration.getPagesize() : pagesize);
    }
}
