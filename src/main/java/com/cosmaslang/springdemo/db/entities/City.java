package com.cosmaslang.springdemo.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cities")
public class City {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private long count;
	private String name;
	private boolean isCapital;
	
	public City() {
		super();
	}

	public City(String name, boolean isCapital) {
		this.name = name;
		this.isCapital = isCapital;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isCapital() {
		return isCapital;
	}
	
	public void setCapital(boolean isCapital) {
		this.isCapital = isCapital;
	}
	
	public long getCount() {
		return count;
	}

	public void setCount(long accessCounter) {
		this.count = accessCounter;
	}

}
