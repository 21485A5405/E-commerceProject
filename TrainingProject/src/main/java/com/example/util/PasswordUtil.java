package com.example.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {
	
	private static final PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	public static String hashPassword(String password) {
		return encoder.encode(password);
	}
	
	public static boolean verifyPassword(String newPassword, String hashedPassword) {
		return encoder.matches(newPassword, hashedPassword);
	}

}
