package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(indexes = @Index(columnList = "name", unique = true))
@EntityListeners(AuditingEntityListener.class)
public class Artist extends TrackDependentEntity implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;

    //muss man leider hier drin definieren, sonst wird es nicht gefunden
    @Column(nullable = false)
    private String name;

    @LastModifiedDate
    @JsonIgnore
    private Date lastModified;

    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
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
}
