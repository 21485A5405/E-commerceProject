package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.Address;
import com.example.model.AdminPermissions;
import com.example.model.LoginDetails;
import com.example.model.Role;
import com.example.model.User;
import com.example.model.UserToken;
import com.example.repo.CartItemRepo;
import com.example.repo.OrderRepo;
import com.example.repo.UserRepo;
import com.example.repo.UserTokenRepo;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{

	private CartItemRepo cartItemRepo;
	private OrderRepo orderRepo;
	private UserRepo userRepo;
	private CurrentUser currentUser;
	private UserTokenRepo userTokenRepo;
	
	public UserServiceImpl(CartItemRepo cartItemRepo, OrderRepo orderRepo, UserRepo userRepo, UserTokenRepo userTokenRepo, CurrentUser currentUser) {
		this.cartItemRepo = cartItemRepo;
		this.orderRepo = orderRepo;
		this.userRepo = userRepo;
		this.currentUser = currentUser;
		this.userTokenRepo = userTokenRepo;
		
	}
	public ResponseEntity<ApiResponse<User>> saveUser(User user) {
		Optional<User> exists = userRepo.findByUserEmail(user.getUserEmail());
		if(exists.isPresent()) {
			throw new CustomException("User Already Exists Please Login");
		}

		if (user.getShippingAddress() != null) {
		    for (Address address : user.getShippingAddress()) {
		        address.setUser(user);
		    }
		} else {
		    user.setShippingAddress(new ArrayList<>());
		}


		if(user.getUserRole()!=null && user.getUserRole()==Role.ADMIN) {
			throw new UnAuthorizedException("You Dont Have Permission to Make User Into Admin");
		}
		else {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String hashedPassword = encoder.encode(user.getUserPassword());
			user.setUserPassword(hashedPassword);
			user.setUserRole(Role.CUSTOMER);
			userRepo.save(user);
		}
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(user);
		response.setMessage("New User Added Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<User>> updateUserById(Long userId, User newUser) {
		
		Optional<User> u = userRepo.findById(userId);
		if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId() != userId) {
			throw new UnAuthorizedException("You Are Not Allowed To Update Another User Details");
		}
		
		if(newUser.getUserName() == null) {
			throw new UserNotFoundException("UserName Cannot be Empty");
		}else if(newUser.getUserEmail() == null) {
			throw new UserNotFoundException("UserEmail Cannot be Empty");
		}else if(newUser.getUserPassword() == null) {
			throw new UserNotFoundException("UserPassword Cannot be Empty");
		}else if(newUser.getUserRole() == null) {
			throw new UserNotFoundException("UserRole Cannot be Empty");
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
		oldUser.setUserRole(newUser.getUserRole());
		
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

		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Allowed to Get Another User Details");
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
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("You Are Not Allowed To Delete User");
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

	public ResponseEntity<ApiResponse<User>> changeUserPassword(String eMail, String newPassword) {
		
	    Optional<User> exists = userRepo.findByUserEmail(eMail);
	    
	    if (!exists.isPresent()) {
	        throw new UnAuthorizedException("Invalid Email");
	    }

	    if (newPassword == null || newPassword.length()<=5) {
	        throw new CustomException("New password cannot be empty or Less Than 5 Characters");
	    }
	    
	    User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= exists.get().getUserId()) {
			throw new UnAuthorizedException("You Are Not Allowed to Change Another User Password");
		}
	    
	    User user = exists.get();
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
		
		if(exists.get().getUserRole()!=Role.CUSTOMER) {
			throw new UnAuthorizedException("Please Provide User Credentials");
		}
		
		User user = exists.get();
		UserToken userToken = new UserToken();
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    if (!encoder.matches(details.getLoginPassword(), user.getUserPassword())) {
	        throw new UnAuthorizedException("Invalid credentials.");
	    }
	    String token = UUID.randomUUID().toString();
	    userToken.setUserToken(token);
	    userToken.setGeneratedAt(LocalDateTime.now());
	    userToken.setUser(user);
	    userTokenRepo.save(userToken);
	    
		ApiResponse<User> response = new ApiResponse<>();
		response.setMessage("Welcome User");
		response.setData(user);
		return ResponseEntity.ok(response);
	}
	
	@Transactional
	public ResponseEntity<ApiResponse<User>> updateUserRole(Set<AdminPermissions> permissions, Long userId, Long adminId) {
		
		Optional<User> exists = userRepo.findById(userId);
		
		if(!exists.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if (currUser.getUserRole() == Role.ADMIN &&
			    !(currUser.getUserPermissions().contains(AdminPermissions.User_Manager) ||
			      currUser.getUserPermissions().contains(AdminPermissions.Manager))) {
			    throw new UnAuthorizedException("You don't have Rights to Update user Roles");
			}

		exists.get().setUserRole(Role.ADMIN);
		exists.get().setUserPermissions(new HashSet<>(permissions));
		
		userRepo.save(exists.get());
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(exists.get());
		response.setMessage("User Role Updated Successfully");
		return ResponseEntity.ok(response);
	}
	
}
