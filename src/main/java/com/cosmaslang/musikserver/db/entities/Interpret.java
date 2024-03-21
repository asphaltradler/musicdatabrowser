package com.cosmaslang.musikserver.db.entities;

import jakarta.persistence.*;

@Entity
@Table(indexes = @Index(columnList = "name", unique = true))
public class Interpret extends NamedEntity {
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
    public void setName(String name) {
        this.name = name;
    }
}
