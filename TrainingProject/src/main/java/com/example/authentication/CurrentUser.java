package com.example.authentication;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.example.model.User;


@Component
@RequestScope
public class CurrentUser {

    private User currentUser;

    public User getUser() { 
    	
    	return currentUser;
    }
    public void setUser(User currentUser) {
    	this.currentUser = currentUser; 
	}
}
