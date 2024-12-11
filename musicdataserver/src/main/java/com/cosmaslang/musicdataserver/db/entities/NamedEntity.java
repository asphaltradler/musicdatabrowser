package com.cosmaslang.musicdataserver.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;
import java.util.Objects;

@NoRepositoryBean
public abstract class NamedEntity implements Comparable<NamedEntity> {
    public abstract long getId();
    public abstract String getName();
    public abstract void setName(String name);

    @LastModifiedDate
    @JsonIgnore
    public abstract Date getLastModified();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedEntity entity = (NamedEntity) o;
        return Objects.equals(getName(), entity.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }


    @Override
    public int compareTo(NamedEntity o) {
        return getName().compareTo(o.getName());
    }
}
