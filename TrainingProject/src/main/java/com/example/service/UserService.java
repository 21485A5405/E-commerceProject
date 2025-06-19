package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.LoginDetails;
import com.example.model.User;

public interface UserService {
	
	public User saveUser(User user);
	
	public User getUserByType(User user);

	public String updateUserById(Long userId, User user);

	public Optional<User> getUserById(Long userId);

	public String deleteUserById(Long userId);

	public List<User> getAllUsers();

	public String changeUserPassword(String eMail, String newPassword);



	

}
