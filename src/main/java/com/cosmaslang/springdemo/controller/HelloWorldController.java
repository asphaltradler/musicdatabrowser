package com.cosmaslang.springdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cosmaslang.springdemo.SpringdemoApplication.SaySomethingService;

@RestController
public class HelloWorldController implements SaySomethingService {

	@GetMapping("hello")
	@Override
	public String saySomething() {
		return "hello from HelloWorldController";
	}
}
