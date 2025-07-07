package com.example.service;

import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.example.DTO.LoginDetails;
import com.example.DTO.RegisterUser;
import com.example.DTO.UpdateUser;
import com.example.controller.ApiResponse;
import com.example.model.AdminPermissions;
import com.example.model.User;

public interface UserService {
	
	public ResponseEntity<ApiResponse<User>> saveUser(RegisterUser user);

	public ResponseEntity<ApiResponse<User>> updateUser(UpdateUser user);

	public ResponseEntity<ApiResponse<User>> getUser();

	public ResponseEntity<ApiResponse<User>> deleteUser();
	
	public ResponseEntity<ApiResponse<User>> changeUserPassword(String eMail, String newPassword);

	public ResponseEntity<ApiResponse<?>> loginUser(LoginDetails details);

	public ResponseEntity<ApiResponse<User>> updateUserRole(Set<AdminPermissions> permissions, Long userId);

}
