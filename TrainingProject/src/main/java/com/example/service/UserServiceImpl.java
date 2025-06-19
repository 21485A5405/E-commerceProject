package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.exceptionfile.CustomException;
import com.example.exceptionfile.UserNotFoundException;
import com.example.model.User;
import com.example.repo.CartItemRepo;
import com.example.repo.OrderRepo;
import com.example.repo.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{	
	
	private CartItemRepo cartItemRepo;
	private OrderRepo orderRepo;
	private UserRepo userRepo;
	
	public UserServiceImpl(CartItemRepo cartItemRepo, OrderRepo orderRepo, UserRepo userRepo) {
		this.cartItemRepo = cartItemRepo;
		this.orderRepo = orderRepo;
		this.userRepo = userRepo;
		
	}
	public User saveUser(User user) {
		return userRepo.save(user);
	}
	
	public User getUserByType(User user) {
		
		return user;
	}

	public String updateUserById(Long userId, User newUser) {
		
		Optional<User> u = userRepo.findById(userId);
		if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		User oldUser = u.get();
		
		oldUser.setUserName(newUser.getUserName());
		oldUser.setUserEmail(newUser.getUserEmail());
		oldUser.setUserPassword(newUser.getUserPassword());
		oldUser.setShippingAddress(newUser.getShippingAddress());
		oldUser.setPaymentDetails(newUser.getPaymentDetails());
		oldUser.setUserType(newUser.getUserType());
		
		userRepo.save(oldUser);
		
		return "New User Details Updated Successfully";
	}

	public Optional<User> getUserById(Long userId) {
		Optional<User> exists = userRepo.findById(userId);
		
		if(!exists.isPresent()) {
		
			throw new UserNotFoundException("User Not Found");
		}
		return exists;
	}

	@Transactional
	public String deleteUserById(Long userId) {
		
		Optional<User> u = userRepo.findById(userId);
		if(!u.isPresent()) {
			
			throw new UserNotFoundException("User Not Found");
		}
		cartItemRepo.deleteAllByUser_UserId(userId);
		orderRepo.deleteAllByUser_UserId(userId);
		userRepo.deleteById(userId);
		
		return "User Deleted Successfully";
	}

	public List<User> getAllUsers() {
		return userRepo.findAll();
	}

	public String changeUserPassword(String eMail, String newPassword) {
	    Optional<User> u = userRepo.findByUserEmail(eMail);

	    if (!u.isPresent()) {
	        throw new UserNotFoundException("Invalid Email");
	    }

	    if (newPassword == null || newPassword.length()<=5) {
	        throw new CustomException("New password cannot be empty or Less Than 5 Characters");
	    }

	    User user = u.get();
	    user.setUserPassword(newPassword);
	    userRepo.save(user);
	    return "User Password Changed Successfully";
	}
}
