package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.exceptionfile.CustomException;
import com.example.exceptionfile.UnAuthorizedException;
import com.example.exceptionfile.UserNotFoundException;
import com.example.model.LoginDetails;
import com.example.model.User;
import com.example.repo.CartItemRepo;
import com.example.repo.OrderRepo;
import com.example.repo.UserRepo;
import com.example.util.PasswordUtil;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{	
	
	private CartItemRepo cartItemRepo;
	private OrderRepo orderRepo;
	private UserRepo userRepo;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
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
	
	public String registerUser(User user) {
		Optional<User> u = userRepo.findByUserEmail(user.getUserEmail());
		
		if(!u.isPresent()) {
			throw new UserNotFoundException("Email Already Exists");
		}
		User newUser = u.get();
		newUser.setUserEmail(user.getUserEmail());
		newUser.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
		newUser.setPaymentDetails(user.getPaymentDetails());
		newUser.setShippingAddress(user.getShippingAddress());
		newUser.setUserType(user.getUserType());
		newUser.setUserName(user.getUserName());
		userRepo.save(newUser);
		
		return "User Registered Successfully";
	}
	
	public String loginUser(LoginDetails userDetails) {
		
		Optional<User> u = userRepo.findByUserEmail(userDetails.getLoginEmail());
		
		if(!u.isPresent()) {
			throw new UserNotFoundException("Email Not Exists");
		}
		if(userDetails.getLoginEmail() == null || userDetails.getLoginPassword() == null) {
			throw new UnAuthorizedException("Email or Password Cannot be Empty");
		}
		User user = u.get();
		return PasswordUtil.verifyPassword(userDetails.getLoginPassword(), user.getUserPassword())
					
										  ? "Login Successfully" :"Invalid Password";
		
	}
	

}
