package com.example.dto;

import com.example.enums.Role;
import com.example.model.User;

public class UpdateUser {

	private Long userId;
	private String userName;
	private String userEmail;
	private Role userRole;
	
	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public UpdateUser() {
    }
	
	public UpdateUser(User user) {
		this.userId = user.getUserId();
		this.userName = user.getUserName();
		this.userEmail = user.getUserEmail();
		this.userRole = user.getUserRole();
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
