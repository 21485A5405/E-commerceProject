package com.example.controller;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.customannotations.ForUser;
import com.example.dto.LoginDetails;
import com.example.dto.RegisterUser;
import com.example.dto.UpdateUser;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.model.User;
import com.example.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
		
	}
	
	@PostMapping("/register-user")
	public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody RegisterUser user) {
		return userService.saveUser(user);
	}
	
	@PostMapping("/login-user")
	public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody LoginDetails details) {
		return userService.loginUser(details);
	}
	@GetMapping("/get-user-by-id/{userId}")
	public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long userId) {
		return userService.getUserById(userId);
	}
	
	@PutMapping("/update-user/{userId}")
	public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long userId, @RequestBody UpdateUser user) {
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
	
	@PutMapping("/update-user-role/{userId}")
	@ForUser(validPermissions = {AdminPermissions.Manager, AdminPermissions.User_Manager},requiredRole = Role.ADMIN, isSelfUser = false)
	public ResponseEntity<ApiResponse<User>> updateRole(@RequestBody Set<AdminPermissions> permissions, @PathVariable Long userId) {
		
		return userService.updateUserRole(permissions, userId);
	}
}