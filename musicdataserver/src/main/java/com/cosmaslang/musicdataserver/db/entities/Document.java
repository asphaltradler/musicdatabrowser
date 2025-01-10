package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.MediaType;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(indexes = {
        @Index(columnList = "name", unique = true),
        @Index(columnList = "hash", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
public class Document extends NamedEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String hash;

    @LastModifiedDate
    @JsonIgnore
    private Date lastModified;

    private String mimeType;

    @JsonIgnore
    //@Lob - bei Postgres nicht nötig
    private byte[] embeddedDocument;

    @JsonIgnore
    private byte[] thumbnail;

    private String externalDocument;

    /**
     * Notwendig für Datenbank-Entities
     */
    public Document() {}

    public Document(byte[] content, String mimeType, String trackName, String suffix) {
        setEmbeddedContent(content, mimeType, trackName, suffix);
    }

    private void setEmbeddedContent(byte[] content, String mimeType, String trackName, String suffix) {
        embeddedDocument = content;
        hash = createHash(content);
        this.mimeType = mimeType;
        setTrackNameForEmbedded(trackName, suffix);
    }

    /**
     * Kann nachträglich noch geändert werden, da es den hash nicht ändert
     */
    public void setTrackNameForEmbedded(String trackName, String suffix) {
        this.name = StringUtils.truncate(trackName, 32) + ": "
                + hash + (StringUtils.isNotBlank(suffix) ? ' ' + suffix : "");
    }

    public Document(File file, String relativePath) {
        setExternalDocument(file, relativePath);
    }

    /**
     * Für ein existierendes Document kann nachträglich eine andere Position aus File
     * gesetzt werden, was dann u.U. auch das hash wieder ändert.
     */
    public void setExternalDocument(File file, String relativePath) {
        externalDocument = relativePath.replace('\\', '/');
        int secondLastIndex = externalDocument.lastIndexOf('/', externalDocument.lastIndexOf('/') - 1);
        name = externalDocument.substring(secondLastIndex + 1);
        hash = createHash(name, file.length());

        String ext = name.substring(name.lastIndexOf('.') + 1);
        if ("pdf".equalsIgnoreCase(ext)) {
            mimeType = MediaType.APPLICATION_PDF.toString();
        } else if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
            mimeType = MediaType.IMAGE_JPEG.toString();
        } else {
            mimeType = "image/" + ext;
        }
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    private String createHash(byte[] content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(content);
            byte[] bytes = md5.digest();
            BigInteger bigInteger = new BigInteger(1, bytes);
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(Arrays.hashCode(content));
        }
    }

    private String createHash(String fileLongName, long fileSize) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(fileLongName.getBytes(StandardCharsets.UTF_8));
            md5.update(String.valueOf(fileSize).getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md5.digest();
            BigInteger bigInteger = new BigInteger(1, bytes);
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(Objects.hash(fileLongName, fileSize));
        }
    }

    public String getHash() {
        return hash;
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

    public byte[] getEmbeddedDocument() {
        return embeddedDocument;
    }

    public String getExternalDocument() {
        return externalDocument;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }
}
