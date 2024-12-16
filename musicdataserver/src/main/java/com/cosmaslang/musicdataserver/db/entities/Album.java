package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(indexes = @Index(columnList = "name", unique = true))
@EntityListeners(AuditingEntityListener.class)
public class Album extends TrackDependentEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;

    //muss man leider hier drin definieren, sonst wird es nicht gefunden
    @Column(nullable = false)
    private String name;

    @LastModifiedDate
    @JsonIgnore
    private Date lastModified;

    @OneToMany(mappedBy = "album", cascade = CascadeType.MERGE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Set<Track> tracks;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    @JsonIgnore
    public Optional<Document> getBooklet() {
        return Optional.ofNullable(getTracks().stream().findFirst().orElseThrow().getBooklet());
    }

    @Nullable
    @JsonProperty
    public Long getBookletId() {
        return getBooklet().map(Document::getId).orElse(null);
    }


    @Nullable
    @JsonProperty
    public String getBookletName() {
        return getBooklet().map(Document::getName).orElse(null);
    }
}
