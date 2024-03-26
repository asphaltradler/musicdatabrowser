package com.cosmaslang.musikserver.controller;

import com.cosmaslang.musikserver.db.entities.Genre;
import com.cosmaslang.musikserver.db.entities.Interpret;
import com.cosmaslang.musikserver.db.entities.Komponist;
import com.cosmaslang.musikserver.db.repositories.AlbumRepository;
import com.cosmaslang.musikserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.logging.Logger;

public abstract class AbstractMusikRestController<T> {
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    TrackRepository trackRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    NamedEntityRepository<Komponist> komponistRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;
    @Autowired
    NamedEntityRepository<Interpret> interpretRepository;

    @GetMapping("/id/{id}")
    protected abstract T findById(@PathVariable Long id);
    @DeleteMapping("/remove")
    protected abstract String remove(@RequestParam() Long id);
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    protected abstract List<T> get(@RequestParam(required = false) String track,
                           @RequestParam(required = false) String album,
                           @RequestParam(required = false) String komponist,
                           @RequestParam(required = false) String werk,
                           @RequestParam(required = false) String genre,
                           @RequestParam(required = false) String interpret,
                           @RequestParam(required = false) Long id);

    protected List<T> getEntitiesIfExists(Long id, CrudRepository<T, Long> repository) {
        Optional<T> entity = repository.findById(id);
        return entity.map(Collections::singletonList).orElse(Collections.emptyList());
    }

    protected T getEntityIfExists(Long id, CrudRepository<T, Long> repository) {
        Optional<T> entity = repository.findById(id);
        if (entity.isPresent()) {
            return entity.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No item found with id " + id);
    }


    protected List<T> getAll(CrudRepository<T, Long> repository) {
        Iterator<T> entityIt = repository.findAll().iterator();
        List<T> entities = new ArrayList<>();
        entityIt.forEachRemaining(entities::add);
        return entities;
    }
}
