package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Document extends NamedEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    @Column(nullable = false)
    private String name;

    @LastModifiedDate
    @JsonIgnore
    private Date lastModified;

    private String mimeType;

    @JsonIgnore
    public byte[] embeddedDocument;
    public File   externalDocument;

    public Document() {
    }

    public Document(byte[] content, String mimeType) {
        this.setContent(content);
        this.mimeType = mimeType;
    }

    public Document(File externalDocument) {
        this.setContent(externalDocument);
        String ext = name.substring(name.lastIndexOf(".") + 1);
        if ("pdf".equalsIgnoreCase(ext)) {
            mimeType = MediaType.APPLICATION_PDF.toString();
        } else if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
            mimeType = MediaType.IMAGE_JPEG.toString();
        } else {
            mimeType = "image/" + ext;
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @JsonIgnore
    public byte[] getContent() throws IOException {
        if (embeddedDocument != null) {
            return embeddedDocument;
        } else if (externalDocument != null) {
            //TODO caching von Images?
            return Files.readAllBytes(externalDocument.toPath());
        }
        return null;
    }

    public void setContent(byte[] content) {
        this.embeddedDocument = content;
        this.name = Long.toHexString(Arrays.hashCode(content));
    }

    public void setContent(File file) {
        this.externalDocument = file;
        Path path = file.toPath();
        this.name = path.subpath(path.getNameCount() - 2, path.getNameCount()).toString()
                .replace('\\', '/');
    }
}
