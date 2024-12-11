package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Document;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@RestController
@CrossOrigin(originPatterns = ALL)
@RequestMapping("/music/document")
public class DocumentRestController {
    private final DocumentRepository documentRepository;

    public DocumentRestController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Document doc = documentRepository.findById(id)
                .orElse(null);
        return HttpResponseHelper.getResponseEntity(doc);
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        Document doc = documentRepository.findById(id)
                .orElse(null);
        return HttpResponseHelper.getResponseEntityForContent(doc);
    }

    @GetMapping("/find")
    public ResponseEntity<?> getDocumentByName(@RequestParam String name) {
        Document doc = documentRepository.findFirstByNameContainsIgnoreCase(name)
                .orElse(null);
        return HttpResponseHelper.getResponseEntityForContent(doc);
    }
}