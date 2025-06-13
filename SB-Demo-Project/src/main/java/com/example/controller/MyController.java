package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
	
	//private Logger log = Loggerfactory.getlogger(MyController.class)
	
	@GetMapping("/welcome")
	public String sayWelcome() {
		return "Hi Brother";
	}
	
	@GetMapping("/hello/{name}")
	public String sayHello(@PathVariable("name") String name) {
		return "Hello "+ name;
	}
}
