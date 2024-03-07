package com.cosmaslang.springdemo.db.entities.music;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "alben")
public class Album {
	
	@jakarta.persistence.Id
	long Id;
	
	private String albumname;
	private String interpret;
	private String komponist;

}
