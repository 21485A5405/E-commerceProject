package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Admin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long adminId;
	private String adminName;
	
	private String adminRole;
	private String adminPermissions;
	
	private String userType;
	
	public Admin() {
		
	}
	
	public Admin(long adminId, String adminName, String adminRole, String adminPermissions) {
		super();
		this.adminId = adminId;
		this.adminName = adminName;
		this.adminRole = adminRole;
		this.adminPermissions = adminPermissions;
	}


	public long getAdminId() {
		return adminId;
	}
	public void setAdminId(long adminId) {
		this.adminId = adminId;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getAdminRole() {
		return adminRole;
	}
	public void setAdminRole(String adminRole) {
		this.adminRole = adminRole;
	}
	public String getAdminPermissions() {
		return adminPermissions;
	}
	public void setAdminPermissions(String adminPermissions) {
		this.adminPermissions = adminPermissions;
	}
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
