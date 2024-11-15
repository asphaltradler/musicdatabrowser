package com.cosmaslang.musikdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"hash", "path"})})
@Table(indexes = {
        @Index(name="hash_idx", columnList = "hash", unique = true),
        @Index(name="path_idx", columnList = "path", unique = true)
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Track extends NamedEntity {
    public static final String FIELDKEY_ORGANIZATION = "ORGANIZATION";
    public static final String FIELDKEY_WORK = "WORK";

    private final static Date DATE_DEFAULT = new Date(0);

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;

    //tag data
    private Integer tracknumber;
    @Column(nullable = false)
    private String name;
    //Bei den ManyToOne dürfen wir NICHT Lazy kaskadieren!!!
    @ManyToOne(cascade = CascadeType.MERGE /*, fetch = FetchType.LAZY*/)
    //@JoinColumn(name = "album_id")
    //@JsonManagedReference
    //@JsonIgnore
    //@JsonIdentityReference(alwaysAsId = true)
    private Album album;
    @ManyToOne(cascade = CascadeType.MERGE)
    //@JoinColumn(name = "komponist_id")
    private Komponist komponist;
    @ManyToOne(cascade = CascadeType.MERGE)
    //@JoinColumn(name = "werk_id")
    private Werk werk;
    //statt EAGER laden wir lazy und definieren startup-Service als @Transactional
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable()
            //name = "interpreten_tracks",
            //joinColumns = @JoinColumn(name = "track_id"),
            //inverseJoinColumns = @JoinColumn(name = "interpret_id"))
    private Set<Interpret> interpreten; // = new HashSet<>();
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable()
            //name = "genre_tracks",
            //joinColumns = @JoinColumn(name = "track_id"),
            //inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres; // = new HashSet<>();
    private String publisher;
    private String publishedDate;
    private String comment;

    //technical data
    @Column(nullable = false)
    private String path;
    @Column(nullable = false)
    private Long size;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, columnDefinition = "TIMESTAMP default '1970-01-01'")
    private Date lastModifiedDate = DATE_DEFAULT;
    private Integer lengthInSeconds;
    private String encoding;
    private Integer samplerate;
    private Long bitrate;
    private Long noOfSamples;
    private Integer bitsPerSample;
    @Column(nullable = false)
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

    @Override
    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Set<Interpret> getInterpreten() {
        return interpreten;
    }

    /**
     * @param interpret fügt einen einzelnen Interpreten hinzu
     */
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

    /**
     * @param genre fügt ein einzelnes Genre hinzu
     */
    public void addGenre(Genre genre) {
        this.genres.add(genre);
        //genre.addTrack(this);
    }

    /**
     * Alle Genres als Liste setzen
     */
    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
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

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
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

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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
