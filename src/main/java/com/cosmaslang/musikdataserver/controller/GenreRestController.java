package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Genre;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/musik/genre")
public class GenreRestController extends AbstractMusikRestController<Genre> {
    @Override
    public Genre findById(@PathVariable Long id) {
        return getEntityIfExists(id, genreRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected List<Genre> get(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        if (id != null) {
            return getEntitiesIfExists(id, genreRepository);
        } else if (genre != null) {
            return genreRepository.findByNameContaining(genre);
        }
        return getAll(genreRepository);
    }
}
