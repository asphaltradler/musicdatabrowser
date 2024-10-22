package com.cosmaslang.musikdataserver.db.entities;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Objects;

@NoRepositoryBean
public abstract class NamedEntity implements Comparable<NamedEntity> {
    public abstract String getName();

    public abstract void setName(String name);

    public NamedEntity() {
    }

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
