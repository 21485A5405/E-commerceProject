package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.Address;
import com.example.model.LoginDetails;
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
	public ResponseEntity<ApiResponse<User>> saveUser(User user) {
		Optional<User> exists = userRepo.findByUserEmail(user.getUserEmail());
		if(exists.isPresent()) {
			throw new CustomException("User Already Exists Please Login");
		}

		for (Address address : user.getShippingAddress()) {
		    address.setUser(user);
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(user.getUserPassword());
		user.setUserPassword(hashedPassword);
		userRepo.save(user);

		ApiResponse<User> response = new ApiResponse<>();
		response.setData(user);
		response.setMessage("New User Added Successfully");
		return ResponseEntity.ok(response);
	}
	
	public User getUserByType(User user) {
		
		return user;
	}

	public ResponseEntity<ApiResponse<User>> updateUserById(Long userId, User newUser) {
		
		Optional<User> u = userRepo.findById(userId);
		if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		if(newUser.getUserName() == null) {
			throw new UserNotFoundException("UserName Cannot be Empty");
		}else if(newUser.getUserEmail() == null) {
			throw new UserNotFoundException("UserEmail Cannot be Empty");
		}else if(newUser.getUserPassword() == null) {
			throw new UserNotFoundException("UserPassword Cannot be Empty");
		}else if(newUser.getUserType() == null) {
			throw new UserNotFoundException("UserType Cannot be Empty");
		}else if(newUser.getShippingAddress() == null) {
			throw new UserNotFoundException("Shipping Address Cannot be Empty");
		}else if(newUser.getPaymentDetails() == null) {
			throw new UserNotFoundException("Payment Details Cannot be Empty");
		}
		User oldUser = u.get();
		
		oldUser.setUserName(newUser.getUserName());
		oldUser.setUserEmail(newUser.getUserEmail());
		oldUser.setUserPassword(newUser.getUserPassword());
		oldUser.setShippingAddress(newUser.getShippingAddress());
		oldUser.setPaymentDetails(newUser.getPaymentDetails());
		oldUser.setUserType(newUser.getUserType());
		
		userRepo.save(oldUser);
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(oldUser);
		response.setMessage("User Updated Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<User>> getUserById(Long userId) {
		Optional<User> exists = userRepo.findById(userId);
		
		if(!exists.isPresent()) {
		
			throw new UserNotFoundException("User Not Found");
		}
		User user = exists.get();
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(user);
		response.setMessage("User Details");
		return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<User>> deleteUserById(Long userId) {
		
		Optional<User> exists = userRepo.findById(userId);
		if(!exists.isPresent()) {
			
			throw new UserNotFoundException("User Not Found");
		}
		cartItemRepo.deleteAllByUser(userId);
		orderRepo.deleteAllByUserId(userId);
		userRepo.deleteById(userId);
		User user = exists.get();
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(user);
		response.setMessage("User Deleted Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		
		List<User> list = userRepo.findAll();
		ApiResponse<List<User>> response = new ApiResponse<>();
		response.setData(list);
		response.setMessage("All Users Details");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<User>> changeUserPassword(String eMail, String newPassword) {
	    Optional<User> u = userRepo.findByUserEmail(eMail);

	    if (!u.isPresent()) {
	        throw new UnAuthorizedException("Invalid Email");
	    }

	    if (newPassword == null || newPassword.length()<=5) {
	        throw new CustomException("New password cannot be empty or Less Than 5 Characters");
	    }

	    User user = u.get();
	    user.setUserPassword(newPassword);
	    userRepo.save(user);
	    ApiResponse<User> response = new ApiResponse<>();
	    response.setData(user);
	    response.setMessage("User Password Changed Successfully");
	    return ResponseEntity.ok(response);
	}
	
	public ResponseEntity<ApiResponse<?>> loginUser(LoginDetails details) {
		
		Optional<User> exists = userRepo.findByUserEmail(details.getLoginEmail());
		
		if(!exists.isPresent()) {
			throw new CustomException("User DoesNot Exists Please Register");
		}
		
		User user = exists.get();
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    if (!encoder.matches(details.getLoginPassword(), user.getUserPassword())) {
	        throw new UnAuthorizedException("Invalid credentials.");
	    }
	    
		ApiResponse<User> response = new ApiResponse<>();
		response.setMessage("Welcome User");
		response.setData(user);
		return ResponseEntity.ok(response);
	}
}
