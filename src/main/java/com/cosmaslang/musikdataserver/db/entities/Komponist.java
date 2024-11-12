package com.cosmaslang.musikdataserver.db.entities;

import jakarta.persistence.*;

@Entity
@Table(indexes = @Index(name="name_idx", columnList = "name", unique = true))
public class Komponist extends NamedEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;

    //muss man leider hier drin definieren, sonst wird es nicht gefunden
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
