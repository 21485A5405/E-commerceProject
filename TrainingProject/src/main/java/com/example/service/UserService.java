package com.example.service;

import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.dto.LoginDetails;
import com.example.dto.RegisterUser;
import com.example.dto.UpdateUser;
import com.example.model.AdminPermissions;
import com.example.model.User;

public interface UserService {
	
	public ResponseEntity<ApiResponse<User>> saveUser(RegisterUser user);

	public ResponseEntity<ApiResponse<User>> updateUserById(Long userId, UpdateUser user);

	public ResponseEntity<ApiResponse<User>> getUserById(Long userId);

	public ResponseEntity<ApiResponse<User>> deleteUserById(Long userId);
	
	public ResponseEntity<ApiResponse<User>> changeUserPassword(String eMail, String newPassword);

	public ResponseEntity<ApiResponse<?>> loginUser(LoginDetails details);

	public ResponseEntity<ApiResponse<User>> updateUserRole(Set<AdminPermissions> permissions, Long userId);

}
