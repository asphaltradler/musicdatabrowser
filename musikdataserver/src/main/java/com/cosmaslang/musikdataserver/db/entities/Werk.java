package com.cosmaslang.musikdataserver.db.entities;

import jakarta.persistence.*;

@Entity
@Table(indexes = @Index(columnList = "name", unique = true))
public class Werk extends NamedEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;

    //muss man leider hier drin definieren, sonst wird es nicht gefunden
    @Column(nullable = false)
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
