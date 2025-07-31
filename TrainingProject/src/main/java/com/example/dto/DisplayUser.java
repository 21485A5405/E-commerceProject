package com.example.dto;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.model.User;
import com.example.model.UserToken;

public class DisplayUser {
	
	private Long userId;
	private String userName;
	private String userEmail;
	private String userToken;
	
	
	    public DisplayUser(User user) {
	        this.userId = user.getUserId();
	        this.userName = user.getUserName();
	        this.userEmail = user.getUserEmail();
	    }
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
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
