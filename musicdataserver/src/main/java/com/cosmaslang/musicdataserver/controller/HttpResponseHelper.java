package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.configuration.MusicDataServerConfiguration;
import com.cosmaslang.musicdataserver.db.entities.Document;
import com.cosmaslang.musicdataserver.db.entities.NamedEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
public class HttpResponseHelper {
    @Autowired
    MusicDataServerConfiguration musicDataServerConfiguration;

    public @NotNull ResponseEntity<?> getResponseEntity(@Nullable NamedEntity entity) {
        HttpHeaders headers = getHttpHeaders(entity);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(entity);
    }

    public @NotNull ResponseEntity<?> getResponseEntityForContent(@Nullable Document doc, boolean useThumbnail) {
        HttpHeaders headers = getHttpHeaders(doc);
        try {
            byte[] thumbnail = useThumbnail ? doc.getThumbnail() : null;
            headers.setContentType(thumbnail != null
                    ? MediaType.IMAGE_GIF
                    : MediaType.valueOf(doc.getMimeType()));
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(
                    thumbnail != null ? thumbnail : getContent(doc));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private byte[] getContent(Document doc) throws IOException {
        if (doc.getEmbeddedDocument() != null) {
            return doc.getEmbeddedDocument();
        } else if (doc.getExternalDocument() != null) {
            return Files.readAllBytes(musicDataServerConfiguration.getFileFromRelativePath(
                    Path.of(doc.getExternalDocument())).toPath());
        }
        return null;
    }

    private @NotNull HttpHeaders getHttpHeaders(NamedEntity entity) {
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(entity.getETag());
        headers.setCacheControl(CacheControl.maxAge(
                musicDataServerConfiguration.getDocumentRefreshTimeInMinutes(), TimeUnit.MINUTES).mustRevalidate());
        headers.setLastModified(entity.getLastModified().getTime());
        return headers;
    }
}
