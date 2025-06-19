package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/adduser")
	public User createUser(@RequestBody User user) {
		return userService.saveUser(user);
	}
	
	@GetMapping("/getuserbyid/{userId}")
	public Optional<User> getUser(@PathVariable Long userId) {
		
		return userService.getUserById(userId);
	}
	
	@GetMapping("/getall")
	public List<User> getAll() {
		return userService.getAllUsers();
	}
	
	@PutMapping("/update/{userId}")
	public String updateUser(@PathVariable Long userId, @RequestBody User user) {
		
		String message = userService.updateUserById(userId, user);
		return message;
	}
	
	@PutMapping("/changepassword/{eMail}/{newPassword}")
	public String changePassword(@PathVariable String eMail, @PathVariable String newPassword) {
		return userService.changeUserPassword(eMail, newPassword);
	}
	
	@DeleteMapping("/deleteuserbyid/{userId}")
	public String deleteUser(@PathVariable Long userId) {
		String message = userService.deleteUserById(userId);
		return message;
	}

}
