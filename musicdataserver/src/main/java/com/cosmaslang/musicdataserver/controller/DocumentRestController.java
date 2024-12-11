package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.Document;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@RestController
@CrossOrigin(originPatterns = ALL)
@RequestMapping("/music/document")
public class DocumentRestController {
    @Autowired
    private HttpResponseHelper httpResponseHelper;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    MusicDataServerConfiguration musicDataServerConfiguration;

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Document doc = documentRepository.findById(id)
                .orElse(null);
        return httpResponseHelper.getResponseEntity(doc);
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        Document doc = documentRepository.findById(id)
                .orElse(null);
        return httpResponseHelper.getResponseEntityForContent(doc);
    }

    @GetMapping("/find")
    public ResponseEntity<?> getDocumentByName(@RequestParam String name) {
        Document doc = documentRepository.findFirstByNameContainsIgnoreCase(name)
                .orElse(null);
        return httpResponseHelper.getResponseEntityForContent(doc);
    }
}