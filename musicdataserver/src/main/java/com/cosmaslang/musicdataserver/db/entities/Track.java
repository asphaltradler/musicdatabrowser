package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.springframework.lang.Nullable;

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
    private long id;

    //tag data
    private Integer tracknumber;
    @Column(nullable = false)
    private String name;
    //Bei den ManyToOne dürfen wir NICHT Lazy kaskadieren!!!
    @ManyToOne(cascade = CascadeType.MERGE /*, fetch = FetchType.LAZY*/)
    //@JoinColumn(name = "album_id")
    @JsonManagedReference
    private Album album;
    @ManyToOne(cascade = CascadeType.MERGE)
    //@JoinColumn(name = "composer_id")
    private Composer composer;
    @ManyToOne(cascade = CascadeType.MERGE)
    //@JoinColumn(name = "work_id")
    private Work work;
    //statt EAGER laden wir lazy und definieren startup-Service als @Transactional
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable()
            //name = "artists_tracks",
            //joinColumns = @JoinColumn(name = "track_id"),
            //inverseJoinColumns = @JoinColumn(name = "artist_id"))
    private Set<Artist> artists; // = new HashSet<>();
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

    //additional data
    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    @Nullable
    private Document albumart;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    @Nullable
    private Document booklet;

    @Nullable
    public Document getAlbumart() {
        return albumart;
    }

    /** Statt des kompletten Dokuments übermitteln wir nur die ID, falls gegeben */
    @Nullable
    @JsonProperty
    public Long getAlbumartId() {
        return albumart == null ? null : albumart.getId();
    }

    @Nullable
    @JsonProperty
    public String getAlbumartName() {
        return albumart == null ? null : albumart.getName();
    }

    public void setAlbumart(@Nullable Document albumArt) {
        this.albumart = albumArt;
    }

    @Nullable
    @JsonProperty
    public Document getBooklet() {
        return booklet;
    }

    @Nullable
    @JsonProperty
    public Long getBookletId() {
        return booklet == null ? null : booklet.getId();
    }

    public void setBooklet(@Nullable Document booklet) {
        this.booklet = booklet;
    }

    @JsonProperty
    public String getPath() {
        return path;
    }

    public void setPath(String file) {
        this.path = file;
    }

    @JsonProperty
    public Integer getTracknumber() {
        return tracknumber;
    }

    public void setTracknumber(Integer tracknumber) {
        this.tracknumber = tracknumber;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
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

    @JsonProperty
    public Set<Artist> getArtists() {
        return artists;
    }

    /**
     * @param artist fügt einen einzelnen artists hinzu
     */
    public void addArtist(Artist artist) {
        this.artists.add(artist);
        //nicht: sonst Endlosschleife
        //artist.addTrack(this);
    }

    /**
     * Alle artists als Liste setzen
     */
    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    @JsonProperty
    public Composer getComposer() {
        return composer;
    }

    public void setComposer(Composer composer) {
        this.composer = composer;
    }

    @JsonProperty
    public Set<Genre> getGenres() {
        return this.genres;
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

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
        //work.addTrack(this);
    }

    @JsonProperty
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @JsonProperty
    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    @JsonProperty
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty
    public Integer getLengthInSeconds() {
        return lengthInSeconds;
    }

    public void setLengthInSeconds(Integer lengthInSeconds) {
        this.lengthInSeconds = lengthInSeconds;
    }

    @JsonProperty
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @JsonProperty
    public Integer getSamplerate() {
        return samplerate;
    }

    public void setSamplerate(Integer samplerate) {
        this.samplerate = samplerate;
    }

    @JsonProperty
    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    @JsonProperty
    public Integer getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(Integer bitrate) {
        this.bitsPerSample = bitrate;
    }

    @JsonProperty
    public Long getNoOfSamples() {
        return noOfSamples;
    }

    public void setNoOfSamples(Long noOfSamples) {
        this.noOfSamples = noOfSamples;
    }

    @JsonProperty
    public Long getSize() {
        return size;
    }

    public void setSize(Long length) {
        this.size = length;
    }

    @JsonProperty
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonProperty
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Track [path=" + path + ", name=" + name + ", album=" + (album == null ? "NULL" : album.getName())
                + ", artists=" + artists.stream().map(Artist::toString).collect(Collectors.joining(","))
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
