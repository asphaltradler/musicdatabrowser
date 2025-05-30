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
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
    @Autowired
    HttpResponseHelper httpResponseHelper;

    protected abstract NamedEntityRepository<ENTITY> getMyRepository();

    @RequestMapping(value = "/find", method = {RequestMethod.GET, RequestMethod.POST})
    protected Page<ENTITY> find(
            @Nullable Integer pageNumber,
            @Nullable Integer pageSize,
            @Nullable String name) {
        logger.fine(String.format("find name=%s, page=%d pageSize=%d", name, pageNumber, pageSize));

        Pageable pageable = getPageableOf(pageNumber, pageSize);
        long time = System.currentTimeMillis();

        Page<ENTITY> page = getMyRepository().findByNameContainsIgnoreCaseOrderByName(name, pageable);

        logger.finer(String.format("page %d of %d: %d of %d elements, in %dms", page.getNumber(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), System.currentTimeMillis() - time));
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
        logger.fine(String.format("findby track=%s, album=%s, composer=%s, work=%s, genre=%s, artist=%s, page=%d pageSize=%d",
                track, album, composer, work, genre, artist, pageNumber, pageSize));
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    protected abstract Page<ENTITY> get(
            Integer pageNumber,
            Integer pageSize,
            Long trackId, Long albumId, Long composerId,
            Long workId, Long genreId, Long artistId);

    protected void logCall(Integer pageNumber, Integer pageSize, Long trackId, Long albumId, Long composerId, Long workId, Long genreId, Long artistId) {
        logger.fine(String.format("get trackId=%d, albumId=%d, composerId=%d, workId=%d, genreId=%d, artistId=%d, page=%d pageSize=%d",
                trackId, albumId, composerId, workId, genreId, artistId, pageNumber, pageSize));
    }

    @RequestMapping(value = "/getall", method = {RequestMethod.GET, RequestMethod.POST})
    public Page<ENTITY> getAll(Pageable pageable) {
        logger.fine("getall");
        return getMyRepository().findAll(pageable);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.fine(String.format("get id=%d", id));
        ENTITY entity = getMyRepository().findById(id)
                .orElse(null);
        return httpResponseHelper.getResponseEntity(entity);
    }

    @Transactional
    @DeleteMapping("/remove/{id}")
    protected void remove(@PathVariable Long id) {
        logger.info(String.format("remove id=%d", id));
        getMyRepository().delete(
                getMyRepository().findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    protected <E extends TrackDependentEntity> void removeFromTrackDependentParent(
            Track track,
            Function<Track, E> mapFunction,
            TrackDependentRepository<E> trackDependentRepository) {
        E entity = mapFunction.apply(track);
        if (entity != null && entity.getTracks().stream().allMatch(track::equals)) {
                //bleibt kein Track außer track mehr übrig? => nicht mehr referenzierte Entity löschen
            trackDependentRepository.delete(entity);
        }
    }

    protected <E extends TrackDependentEntity> void removeFromTrackDependentParents(
            Track track,
            Function<Track, Set<E>> mapFunction,
            TrackDependentRepository<E> trackDependentRepository) {
        Set<E> entities = mapFunction.apply(track);
        entities.forEach(entity -> {
            if (entity.getTracks().stream().allMatch(track::equals)) {
                //bleibt kein Track außer track mehr übrig? => nicht mehr referenzierte Entity löschen
                trackDependentRepository.delete(entity);
            }
        });
    }

    protected <SRC extends TrackDependentEntity, DEST extends TrackDependentEntity>
        void removeDependentParent(
            SRC sourceEntity,
            Function<Track, DEST> mapFunction,
            TrackDependentRepository<DEST> trackDependentRepository) {
        Set<Track> tracks = Optional.ofNullable(sourceEntity).map(TrackDependentEntity::getTracks).orElse(Collections.emptySet());
        List<DEST> deletes = new ArrayList<>();
        Stream<DEST> entities = tracks.stream().map(mapFunction).filter(Objects::nonNull).distinct();
        entities.forEach(entity -> {
            //ausschließlich die Tracks aus SRC enthalten?
            if (tracks.containsAll(entity.getTracks())) {
                deletes.add(entity);
            }
        });
        trackDependentRepository.deleteAll(deletes);
    }

    protected <SRC extends TrackDependentEntity, DEST extends TrackDependentEntity>
    void removeDependentParents(
            SRC sourceEntity,
            Function<Track, Set<DEST>> mapFunction,
            TrackDependentRepository<DEST> trackDependentRepository) {
        //FALSCH
        Set<Track> tracks = Optional.ofNullable(sourceEntity).map(TrackDependentEntity::getTracks).orElse(Collections.emptySet());
        List<DEST> deletes = new ArrayList<>();
        Stream<Set<DEST>> entitySets = tracks.stream().map(mapFunction).filter(Objects::nonNull).distinct();
        entitySets.forEach(entitySet -> entitySet.stream().distinct().forEach(entity -> {
            if (tracks.containsAll(entity.getTracks())) {
                deletes.add(entity);
            }
        }));
        trackDependentRepository.deleteAll(deletes);
    }

    protected Pageable getPageableOf(Integer pageNumber, Integer pageSize) {
        return PageRequest.of(pageNumber == null ? 0 : pageNumber,
                pageSize == null ? musicDataServerConfiguration.getPageSize() : pageSize);
    }
}
