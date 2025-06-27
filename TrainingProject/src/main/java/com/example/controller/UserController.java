package com.example.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.LoginDetails;
import com.example.model.User;
import com.example.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register-user")
	public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
		return userService.saveUser(user);
	}
	
	@PostMapping("/user-login")
	public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody LoginDetails details) {
		return userService.loginUser(details);
	}
	@GetMapping("/get-user-by-id/{userId}")
	public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long userId) {
		return userService.getUserById(userId);
	}
	
	@GetMapping("/getall")
	public ResponseEntity<ApiResponse<List<User>>> getAll() {
		return userService.getAllUsers();
	}
	
	@PutMapping("/update-user/{userId}")
	public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long userId, @RequestBody User user) {
		return userService.updateUserById(userId, user);
	}
	
	@PutMapping("/change-password/{eMail}/{newPassword}")
	public ResponseEntity<ApiResponse<User>> changePassword(@PathVariable String eMail, @PathVariable String newPassword) {
		return userService.changeUserPassword(eMail, newPassword);
	}
	
	@DeleteMapping("/delete-user-by-id/{userId}")
	public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable Long userId) {
		return userService.deleteUserById(userId);
		
	}
}