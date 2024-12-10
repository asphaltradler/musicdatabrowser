package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class TrackDependentEntity extends NamedEntity{
    @JsonBackReference
    public abstract Set<Track> getTracks();

    @JsonBackReference
    public Optional<Document> getAlbumart() {
        return getTracks().stream().map(Track::getAlbumart).filter(Objects::nonNull).findFirst();
    }

    /** Statt des kompletten Dokuments übermitteln wir nur die ID, falls gegeben */
    @Nullable
    @JsonProperty
    public Long getAlbumartId() {
        return getAlbumart().map(Document::getId).orElse(null);
    }

    @Nullable
    @JsonProperty
    public String getAlbumartName() {
        return getAlbumart().map(Document::getName).orElse(null);
    }
}
