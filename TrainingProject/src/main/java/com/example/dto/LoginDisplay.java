package com.example.dto;

import com.example.model.UserToken;

public class LoginDisplay {

	private Long userId;
	private String userToken;
	
	public LoginDisplay(UserToken userToken) {
		
		this.userId = userToken.getUser().getUserId();
		this.userToken = userToken.getUserToken();
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
}
