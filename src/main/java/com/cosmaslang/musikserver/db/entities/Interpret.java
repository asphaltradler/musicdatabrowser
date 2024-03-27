package com.cosmaslang.musikserver.db.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = @Index(columnList = "name", unique = true))
public class Interpret extends NamedEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;
    //muss man leider hier drin definieren, sonst wird es nicht gefunden
    private String name;

    @ManyToMany(mappedBy = "interpreten", fetch = FetchType.EAGER)
    private Set<Track> tracks = new HashSet<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        //NICHT track.addInterpret() sonst gibt es eine Endlosschleife!
        //track.getInterpreten().add(this);
    }
}
