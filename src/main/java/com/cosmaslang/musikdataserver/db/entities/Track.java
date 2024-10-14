package com.cosmaslang.musikdataserver.db.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(indexes = {@Index(columnList = "path", unique = true),
        @Index(columnList = "hash", unique = true)})
public class Track extends NamedEntity {

    public static final String FIELDKEY_ORGANIZATION = "ORGANIZATION";
    public static final String FIELDKEY_WORK = "WORK";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    //tag data
    private Integer tracknumber;
    private String name;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "album_id")
    private Album album;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "komponist_id")
    private Komponist komponist;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "werk_id")
    private Werk werk;
    //EAGER ist hier wichtig!
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "interpreten_tracks",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "interpret_id"))
    private Set<Interpret> interpreten = new HashSet<>();
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "genre_tracks",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();
    private String publisher;
    private String comment;

    //technical data
    private String path;
    private Long size;
    private Integer lengthInSeconds;
    private String encoding;
    private Integer samplerate;
    private Long bitrate;
    private Long noOfSamples;
    private Integer bitsPerSample;
    private String hash;

    public String getPath() {
        return path;
    }

    public void setPath(String file) {
        this.path = file;
    }

    public Integer getTracknumber() {
        return tracknumber;
    }

    public void setTracknumber(Integer tracknumber) {
        this.tracknumber = tracknumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
        //album.addTrack(this);
    }

    public Set<Interpret> getInterpreten() {
        return interpreten;
    }

    public void addInterpret(Interpret interpret) {
        this.interpreten.add(interpret);
        //nicht: sonst Endlosschleife
        //interpret.addTrack(this);
    }

    /**
     * Alle Interpreten als Liste setzen
     */
    public void setInterpreten(Set<Interpret> interpreten) {
        this.interpreten = interpreten;
    }

    public Komponist getKomponist() {
        return komponist;
    }

    public void setKomponist(Komponist komponist) {
        this.komponist = komponist;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
        //genre.addTrack(this);
    }

    public Werk getWerk() {
        return werk;
    }

    public void setWerk(Werk werk) {
        this.werk = werk;
        //werk.addTrack(this);
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getLengthInSeconds() {
        return lengthInSeconds;
    }

    public void setLengthInSeconds(Integer lengthInSeconds) {
        this.lengthInSeconds = lengthInSeconds;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getSamplerate() {
        return samplerate;
    }

    public void setSamplerate(Integer samplerate) {
        this.samplerate = samplerate;
    }

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    public Integer getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(Integer bitrate) {
        this.bitsPerSample = bitrate;
    }

    public Long getNoOfSamples() {
        return noOfSamples;
    }

    public void setNoOfSamples(Long noOfSamples) {
        this.noOfSamples = noOfSamples;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long length) {
        this.size = length;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Track [path=" + path + ", name=" + name + ", album=" + (album == null ? "NULL" : album.getName())
                + ", interpreten=" + interpreten.stream().map(Interpret::toString).collect(Collectors.joining(","))
                + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track entity = (Track) o;
        return Objects.equals(getHash(), entity.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHash());
    }

}
