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
@RequestMapping({"/", "/music", "music/"})
//CORS
@CrossOrigin(originPatterns = ALL)
public class HelloController {
    @Autowired
    NamedEntityRepository<Track> trackRepository;
    @Autowired
    NamedEntityRepository<Artist> artistRepository;
    @Autowired
    NamedEntityRepository<Album> albumRepository;
    @Autowired
    NamedEntityRepository<Work> workRepository;
    @Autowired
    NamedEntityRepository<Genre> genreRepository;
    @Autowired
    NamedEntityRepository<Composer> composerRepository;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String get() {
        long count = trackRepository.count();
        if (count > 0) {
            return String.format("MusicRepository enthält %d tracks mit %d Alben, %d Komponisten, %d Werke, %d Genres, %d Interpreten\n",
                    count, albumRepository.count(), composerRepository.count(), workRepository.count(), genreRepository.count(), artistRepository.count());
        } else {
            return "Service gestartet aber noch leer.";
        }
    }
}
