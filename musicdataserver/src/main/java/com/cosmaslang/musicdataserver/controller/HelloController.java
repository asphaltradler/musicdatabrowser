package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.services.MusicDataServerStartupService;
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
    MusicDataServerStartupService musicDataServerStartupService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String get() {
        return musicDataServerStartupService.getInfo();
    }
}
