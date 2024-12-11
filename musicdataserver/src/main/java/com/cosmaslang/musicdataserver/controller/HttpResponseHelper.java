package com.cosmaslang.musicdataserver.controller;

import com.cosmaslang.musicdataserver.db.entities.Document;
import com.cosmaslang.musicdataserver.db.entities.NamedEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HttpResponseHelper {
    public @NotNull static ResponseEntity<?> getResponseEntity(@Nullable NamedEntity entity) {
        HttpHeaders headers = getHttpHeaders(entity);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(entity);
    }

    public @NotNull static ResponseEntity<?> getResponseEntityForContent(@Nullable Document doc) {
        HttpHeaders headers = getHttpHeaders(doc);
        try {
            headers.setContentType(MediaType.valueOf(doc.getMimeType()));
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(doc.getContent());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private static @NotNull HttpHeaders getHttpHeaders(NamedEntity entity) {
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        //headers.set(HttpHeaders.LAST_MODIFIED, df.format(doc.getLastModified()) + " GMT");
        headers.setETag('\"' + Long.toHexString(Objects.hashCode(entity.getLastModified())) + '\"');
        headers.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).mustRevalidate());
        headers.setLastModified(entity.getLastModified().getTime());
        return headers;
    }
}
