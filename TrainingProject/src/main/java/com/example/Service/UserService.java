package com.example.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.User;
import com.example.Repository.UserRepo;

@Service
public class UserService {
	
	
	@Autowired
	private UserRepo userRepo;
	
	public User saveUser(User user) {
		return userRepo.save(user);
	}
	
	public Optional<User> getProductById(Long userId) {
			
		    return userRepo.findById(userId);
	}
}
