package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.*;
import com.cosmaslang.musicdataserver.db.repositories.NamedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@RestController
@RequestMapping({"/", "/musik", "musik/"})
//CORS
@CrossOrigin(originPatterns = ALL)
public class HelloController {
    @Autowired
    NamedEntityRepository<Track> trackRepository;
    @Autowired
    NamedEntityRepository<Interpret> interpretRepository;
    @Autowired
    NamedEntityRepository<Album> albumRepository;
    @Autowired
    NamedEntityRepository<Werk> werkRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;
    @Autowired
    NamedEntityRepository<Komponist> komponistRepository;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String get() {
        long count = trackRepository.count();
        if (count > 0) {
            return String.format("MusikRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Interpreten, %d Werke, %d Genres",
                    count, albumRepository.count(), komponistRepository.count(), interpretRepository.count(), werkRepository.count(), genreRepository.count());
        } else {
            return "Service gestartet aber noch leer.";
        }
    }
}
