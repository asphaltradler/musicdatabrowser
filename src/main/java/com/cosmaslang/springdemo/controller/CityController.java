package com.cosmaslang.springdemo.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cosmaslang.springdemo.db.CityRepository;
import com.cosmaslang.springdemo.db.entities.City;

@RestController
public class CityController {
	@Autowired
	CityRepository cityRepository;
	
	@GetMapping("/city")
	public List<City> getCities(@RequestParam(value = "name", required = false) String name) {
		List<City> cities;
		if (StringUtils.hasLength(name)) {
			cities = cityRepository.findByName(name);
			cities.forEach(this::increaseAccessCounter);
		} else {
			Iterator<City> allCities = cityRepository.findAll().iterator();
			cities = new ArrayList<City>();
			allCities.forEachRemaining(cities::add);
		}
		return cities;
	}
	
	@GetMapping("/add")
	public String addCity(@RequestParam(value = "name", required = true) String name) {
		int count = cityRepository.findByName(name).size();
		City city = new City(name, false);
		cityRepository.save(city);
		if (count == 0) {
			return name + " created";
		} else {
			return name + " added, already existed " + count + " times";
		}
	}

	private void increaseAccessCounter(City city) {
		city.setAccessCounter(city.getAccessCounter() + 1);
		cityRepository.save(city);
	}
	
}
