package com.example.dto;

import java.util.List;
import java.util.Set;

import com.example.enums.AdminPermissions;
import com.example.model.Address;
import com.example.model.PaymentInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class RegisterAdmin {

    private String userName;
    @Email(message = "Enter Valid Email")
    private String userEmail;
    private String userPassword;

    @NotNull(message = "Permissions are required")
    private Set<AdminPermissions> userPermissions;

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

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Set<AdminPermissions> getUserPermissions() {
		return userPermissions;
	}

	public void setUserPermissions(Set<AdminPermissions> userPermissions) {
		this.userPermissions = userPermissions;
	}
    
    
}
