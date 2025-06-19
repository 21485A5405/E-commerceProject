package com.example.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.LoginDetails;
import com.example.model.User;
import com.example.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
	
	private UserService userService;
	
	public AuthorizationController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public String registerUser(@RequestBody User user) {
		return userService.registerUser(user);
	}
	
	@PostMapping("/login")
	public String loginUser(@PathVariable LoginDetails userDetails) {
		return userService.loginUser(userDetails);
	}
	

}
