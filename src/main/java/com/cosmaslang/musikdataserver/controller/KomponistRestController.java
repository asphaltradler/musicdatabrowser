package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.Komponist;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/musik/komponist")
public class KomponistRestController extends AbstractMusikRestController<Komponist> {
    @Override
    public Komponist findById(@PathVariable Long id) {
        return getEntityIfExists(id, komponistRepository);
    }

    @Override
    protected String remove(Long id) {
        return null;
    }

    @Override
    protected List<Komponist> get(String track, String album, String komponist, String werk, String genre, String interpret, Long id) {
        if (id != null) {
            return getEntitiesIfExists(id, komponistRepository);
        } else if (komponist != null) {
            return komponistRepository.findByNameContaining(komponist);
        }
        return getAll(komponistRepository);
    }
}
