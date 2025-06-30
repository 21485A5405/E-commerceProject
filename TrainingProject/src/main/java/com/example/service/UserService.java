package com.example.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.model.LoginDetails;
import com.example.model.User;

public interface UserService {
	
	public ResponseEntity<ApiResponse<User>> saveUser(User user);
	
	public User getUserByType(User user);

	public ResponseEntity<ApiResponse<User>> updateUserById(Long userId, User user);

	public ResponseEntity<ApiResponse<User>> getUserById(Long userId);

	public ResponseEntity<ApiResponse<User>> deleteUserById(Long userId);

//	public ResponseEntity<ApiResponse<List<User>>> getAllUsers();

	public ResponseEntity<ApiResponse<User>> changeUserPassword(String eMail, String newPassword);

	public ResponseEntity<ApiResponse<?>> loginUser(LoginDetails details);

//	public User validateUser(String token, Long userId);

	

}
