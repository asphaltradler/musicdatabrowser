package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Document;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@RestController
@CrossOrigin(originPatterns = ALL)
@RequestMapping("/music/document")
public class DocumentRestController {
    @Autowired
    HttpResponseHelper httpResponseHelper;
    @Autowired
    DocumentRepository documentRepository;

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.fine(String.format("get id=%d", id));
        Document entity = documentRepository.findById(id)
                .orElse(null);
        return httpResponseHelper.getResponseEntity(entity);
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        logger.fine(String.format("getContent id=%d", id));
        Document doc = documentRepository.findById(id)
                .orElse(null);
        return httpResponseHelper.getResponseEntityForContent(doc);
    }
}