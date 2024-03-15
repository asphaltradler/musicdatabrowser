package com.cosmaslang.musikserver.db.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
//@Table(name = "track")
public class Track {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	private String path;
	private Integer tracknumber;
	private String title;
	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name="album_id")
	private Album album;
	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name="komponist_id")
	private Komponist komponist;
	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name="genre_id")
	private Genre genre;
	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name="werk_id")
	private Werk werk;
	//EAGER ist hier wichtig!
	@ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(
			name = "interpreten_tracks",
			joinColumns = @JoinColumn(name = "track_id"),
			inverseJoinColumns = @JoinColumn(name = "interpret_id"))
	Set<Interpret> interpreten = new HashSet<>();
	private String publisher;
	private String comment;

	public Werk getWerk() {
		return werk;
	}

	public void setWerk(Werk werk) {
		this.werk = werk;
	}

	public Integer getLengthInSeconds() {
		return lengthInSeconds;
	}

	public void setLengthInSeconds(Integer lengthInSeconds) {
		this.lengthInSeconds = lengthInSeconds;
	}

	private Integer lengthInSeconds;
	private String encoding;
	private Integer samplerate;
	private Integer bitsPerSample;

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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public void setInterpreten(Set<Interpret> interpret) {
		this.interpreten = interpret;
	}
	public void addInterpret(Interpret interpret) {
        this.interpreten.add(interpret);
	}
	public Komponist getKomponist() {
		return komponist;
	}
	public void setKomponist(Komponist komponist) {
		this.komponist = komponist;
	}
	public Genre getGenre() {
		return genre;
	}
	public void setGenre(Genre genre) {
		this.genre = genre;
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
	public Integer getBitsPerSample() {
		return bitsPerSample;
	}
	public void setBitsPerSample(Integer bitrate) {
		this.bitsPerSample = bitrate;
	}

	@Override
	public String toString() {
		return "Track [path=" + path + ", title=" + title + ", album=" + album.getName()
				+ ", interpreten=" + interpreten.stream().map(Interpret::toString).collect(Collectors.joining(","))
				+ "]";
	}

}
