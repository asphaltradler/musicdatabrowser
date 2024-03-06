package com.cosmaslang.springdemo.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cosmaslang.springdemo.db.entities.City;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {
	List<City> findByName(String name);
}
