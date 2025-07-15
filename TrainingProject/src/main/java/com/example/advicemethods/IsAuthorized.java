package com.example.advicemethods;

import java.util.Set;

import com.example.enums.AdminPermissions;
import com.example.enums.Role;

public class IsAuthorized {
	
	
	public static boolean isAdmin(Role role) {
		
		return Role.ADMIN == role;
	}
	public static boolean isManager(Set<AdminPermissions> permission) {
		
		return permission.contains(AdminPermissions.Manager);
	}
	
	public static boolean isProductManager(Set<AdminPermissions> permission) {
		
		return permission.contains(AdminPermissions.Product_Manager);
	}
	
	public static boolean isOrderManager(Set<AdminPermissions> permission) {
		
		return permission.contains(AdminPermissions.Order_Manager);
	}
	
	public static boolean isUserManager(Set<AdminPermissions> permission) {
		
		return permission.contains(AdminPermissions.User_Manager);
	}
	
	public static boolean isSalesManager(Set<AdminPermissions> permission) {
		
		return permission.contains(AdminPermissions.Sales_Manager);
	}

}
