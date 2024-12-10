package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Document;
import com.cosmaslang.musicdataserver.db.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@RestController
@CrossOrigin(originPatterns = ALL)
@RequestMapping("/music/document")
public class DocumentRestController {
    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/id/{id}")
    public Document getById(@PathVariable Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        try {
            Document doc = documentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(doc.getMimeType()))
                    //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getName() + "\"")
                    .header(HttpHeaders.LOCATION, doc.getName())
                    .cacheControl(CacheControl.maxAge(10, TimeUnit.DAYS))
                    .body(doc.getContent());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/find")
    public ResponseEntity<?> getDocumentByName(@RequestParam String name) {
        try {
            Document doc = documentRepository.findFirstByNameContainsIgnoreCase(name)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(doc.getMimeType()))
                    //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getName() + "\"")
                    .header(HttpHeaders.LOCATION, doc.getName())
                    .cacheControl(CacheControl.maxAge(10, TimeUnit.DAYS))
                    .body(doc.getContent());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}