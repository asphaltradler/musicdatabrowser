package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.MediaType;

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
    public String externalDocument;

    public Document() {
    }

    public Document(byte[] content, String mimeType) {
        this.setEmbeddedDocument(content);
        this.mimeType = mimeType;
    }

    public Document(String externalDocumentPath) {
        this.setExternalDocument(externalDocumentPath);
        String ext = name.substring(name.lastIndexOf('.') + 1);
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

    public void setEmbeddedDocument(byte[] content) {
        this.embeddedDocument = content;
        this.name = "Embedded #" + Long.toHexString(Arrays.hashCode(content)).toUpperCase();
    }

    public void setExternalDocument(String path) {
        this.externalDocument = path.replace('\\', '/');
        int secondLastIndex = externalDocument.lastIndexOf('/', externalDocument.lastIndexOf('/') - 1);
        this.name = externalDocument.substring(secondLastIndex + 1);
    }
}
