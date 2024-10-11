package com.cosmaslang.musikdataserver.db.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = @Index(columnList = "name", unique = true))
public class Genre extends NamedEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;
    //muss man leider hier drin definieren, sonst wird es nicht gefunden
    private String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.EAGER)
    private Set<Track> tracks = new HashSet<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        //track.getGenres().add(this);
    }

}
