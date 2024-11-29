package com.cosmaslang.musicdataserver.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AlbumArt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    String imageName;
    String embeddedImage;
}
