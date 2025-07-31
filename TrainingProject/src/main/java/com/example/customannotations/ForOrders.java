package com.example.customannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.enums.AdminPermissions;
import com.example.enums.Role;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForOrders {
	
    Role requiredRole();
    AdminPermissions[] validPermissions() default {}; // array - empty by starting
}