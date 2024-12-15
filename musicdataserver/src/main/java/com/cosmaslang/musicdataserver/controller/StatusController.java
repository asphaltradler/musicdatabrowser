package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.services.MusicDataServerStartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@RestController
@RequestMapping({"/", "/music", "/music/status"})
//CORS
@CrossOrigin(originPatterns = ALL)
public class StatusController {
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    MusicDataServerStartupService musicDataServerStartupService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String get() {
        logger.fine("Status request");
        return musicDataServerStartupService.getInfo();
    }
}
