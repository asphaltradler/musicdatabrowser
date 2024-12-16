package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public abstract class TrackDependentEntity extends NamedEntity {
    @JsonBackReference
    public abstract Set<Track> getTracks();

    @JsonIgnore
    public Optional<Document> getAlbumart() {
        return getTracks().stream().map(Track::getAlbumart).filter(Objects::nonNull).findFirst();
    }

    /** Statt des kompletten Dokuments übermitteln wir nur die ID, falls gegeben */
    @JsonProperty
    public Optional<Long> getAlbumartId() {
        return getAlbumart().map(Document::getId);
    }

    @JsonProperty
    public Optional<String> getAlbumartName() {
        return getAlbumart().map(Document::getName);
    }
}
