package com.cosmaslang.springdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cosmaslang.springdemo.SpringdemoApplication.SaySomethingService;

@RestController
public class SaySomethingController {
	@Autowired
	@Qualifier("sayHelloService")
	SaySomethingService service;
	
	@GetMapping("/")
	public String root() {
		return service.saySomething();
	}
}
