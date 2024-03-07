package com.cosmaslang.springdemo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cosmaslang.springdemo.db.CityRepository;
import com.cosmaslang.springdemo.db.entities.City;

@RestController
@RequestMapping("/city")
public class CityController {
	@Autowired
	CityRepository cityRepository;
	
	@RequestMapping(value = "/get", method = { RequestMethod.GET, RequestMethod.POST })
	public List<City> getCities(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "count", required = false) Long count,
			@RequestParam(value = "id", required = false) Long id) {
		List<City> cities;
		if (StringUtils.hasLength(name)) {
			cities = cityRepository.findByName(name);
		} else if (id != null) {
			cities = Collections.singletonList(cityRepository.findById(id).orElse(null));
			//cities.forEach(this::increaseCount);
		} else if (count != null) {
			cities = cityRepository.findByCount(count);
		} else {
			Iterator<City> allCities = cityRepository.findAll().iterator();
			cities = new ArrayList<City>();
			allCities.forEachRemaining(cities::add);
		}
		return cities;
	}
	
	@GetMapping("/id/{id}")
	public City findById(@PathVariable Long id) {
		Optional<City> city = cityRepository.findById(id);
		if (city.isPresent()) {
			return city.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No City found with id " + id);
	}
	
	@GetMapping("/count/{count}")
	public List<City> findAllByCount(@PathVariable Long count) {
		List<City> cities = cityRepository.findByCount(count);
		if (cities.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No City found with count " + count);
		}
		return cities;
	}
	
	@PutMapping("/add")
	public String addCity(@RequestParam(value = "name", required = true) String name) {
		City city = new City(name, false);
		return add(city);
	}
	
	@PostMapping("/create")
	public String createCity(@RequestBody City city) {
		return add(city);
	}

	private String add(City city) {
		String name = city.getName();
		List<City> cities = cityRepository.findByName(name);
		if (!cities.isEmpty()) {
			city = cities.get(0);
			city.setCount(city.getCount() + 1);
		}
		cityRepository.save(city);
		String result = city.getName() + " added, count=" +city.getCount();
		System.out.println(result);
		return result;
	}
	
}
