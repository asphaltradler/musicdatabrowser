package com.cosmaslang.musikdataserver.controller;

import com.cosmaslang.musikdataserver.db.entities.*;
import com.cosmaslang.musikdataserver.db.repositories.NamedEntityRepository;
import com.cosmaslang.musikdataserver.db.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
//CORS
@CrossOrigin(originPatterns = "http://localhost:42*")
@RequestMapping({"/", "/musik"})
public class HelloController {
    @Autowired
    TrackRepository trackRepository;
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
        if (trackRepository.count() > 0) {
            return String.format("MusikRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Interpreten, %d Werke, %d Genres",
                    trackRepository.count(), albumRepository.count(), komponistRepository.count(), interpretRepository.count(), werkRepository.count(), genreRepository.count());
        } else {
            return "Service gestartet aber noch leer.";
        }
    }
}
