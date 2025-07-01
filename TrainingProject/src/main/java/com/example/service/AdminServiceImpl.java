package com.example.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.exception.AdminNotFoundException;
import com.example.exception.CustomException;
import com.example.exception.UnAuthorizedException;
import com.example.model.LoginDetails;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.Role;
import com.example.model.User;
import com.example.model.UserToken;
import com.example.repo.OrderRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;
import com.example.repo.UserTokenRepo;

@Service
public class AdminServiceImpl implements AdminService{
	
	private UserRepo userRepo;
	private ProductRepo productRepo;
	private OrderRepo orderRepo;
	private CurrentUser currentUser;
	private UserTokenRepo userTokenRepo;
	
	public AdminServiceImpl(UserRepo userRepo, ProductRepo productRepo, OrderRepo orderRepo, UserTokenRepo userTokenRepo, CurrentUser currentUser) {
		this.userRepo = userRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
		this.currentUser = currentUser;
		this.userTokenRepo = userTokenRepo;
	}
	
	public ResponseEntity<ApiResponse<User>> createAdmin(User newAdmin) {
		Optional<User> exists = userRepo.findByUserEmail(newAdmin.getUserName());
		
		if(exists.isPresent() && exists.get().getUserRole().equals(newAdmin.getUserRole())){
			throw new CustomException("Admin Already Exists Please Login");
		}
		
		if(!exists.get().getUserRole().equals(Role.ADMIN)) {
			throw new AdminNotFoundException("User Not Allowed To Create Admin");
		}
		
		userRepo.save(newAdmin);
		User admin = exists.get();
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(admin);
		response.setMessage("New Admin Added Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<User>> getAdminById(Long adminId) {
		
		Optional<User> exists = userRepo.findById(adminId);
		if(!exists.isPresent()) {
			throw new AdminNotFoundException("Admin Not Found");
		}
		
		if(!exists.get().getUserRole().equals(Role.ADMIN)) {
			throw new AdminNotFoundException("User Not Allowed To See Admin Details");
		}
		
		User currUser = currentUser.getUser();
		if(currUser.getUserId() != adminId) {
			throw new UnAuthorizedException("Not Authorized");
		}
		
		User admin = exists.get();
		ApiResponse<User> adminFound = new ApiResponse<>();
		adminFound.setData(admin);
		adminFound.setMessage("Admin Details");
		return ResponseEntity.ok(adminFound);
	}

	public ResponseEntity<User> updateAdminById(Long adminId, User newAdmin) {
		
		Optional<User> exists = userRepo.findById(adminId);
		if(!exists.isPresent()) {
			
			throw new AdminNotFoundException("Admin Not Found");
		}

		if(!exists.get().getUserRole().equals(Role.ADMIN)) {
			throw new AdminNotFoundException("User Not Allowed To Update Admin Details");
		}
		
		User currUser = currentUser.getUser();
		if(currUser.getUserId() != adminId) {
			throw new UnAuthorizedException("Not Authorized");
		}
		
		if(newAdmin.getUserName() == null) {
			throw new AdminNotFoundException("Admin Name Cannot be Empty");
		}else if(newAdmin.getPermissions() == null) {
			throw new AdminNotFoundException("Admin Permissions Cannot be Empty");
		}else if(newAdmin.getUserRole() == null) {
			throw new AdminNotFoundException("Admin Role Cannot be Empty");
		}else if(newAdmin.getUserEmail() == null) {
			throw new AdminNotFoundException("Email Cannot be Empty");
		}else if(newAdmin.getUserPassword() == null) {
			throw new AdminNotFoundException("Password Caanot be Empty");
		}
		
		User oldAdmin = exists.get();
		oldAdmin.setUserName(newAdmin.getUserName());
		oldAdmin.setPermissions(newAdmin.getPermissions());
		oldAdmin.setUserRole(newAdmin.getUserRole());
		
		userRepo.save(oldAdmin);
		return ResponseEntity.status(HttpStatus.OK).body(oldAdmin);
	}
	
	public ResponseEntity<ApiResponse<User>> deleteAdminById(Long adminId) {
		
		Optional<User> exists = userRepo.findById(adminId);
		if(!exists.isPresent()) {
			
			throw new AdminNotFoundException("Admin Not Found");
		}
		
		if(!exists.get().getUserRole().equals(Role.ADMIN)) {
			throw new AdminNotFoundException("User Not Allowed To Delete Admin Details");
		}
		
		userRepo.deleteById(adminId);
		User admin = exists.get();
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(admin);
		response.setMessage("Admin Deleted Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<User>>> getAllAdmins() {
		List<User> list = userRepo.findAll();
		List<User> admins = new ArrayList<>();
		
		for(User users :list) {
			if(users.getUserRole() == Role.ADMIN) {
				admins.add(users);
			}
		}

		User currUser = currentUser.getUser();
		if(!currUser.getUserRole().equals(Role.ADMIN)) {
			throw new UnAuthorizedException("User are Not Allowed to See Admin Details");
		}
		ApiResponse<List<User>> response = new ApiResponse<>();
		response.setMessage("List Of Admins");
		response.setData(admins);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders() {
		List<OrderProduct> orderList = orderRepo.findAll();
		
		User currUser = currentUser.getUser();
		if(!currUser.getUserRole().equals(Role.ADMIN)) {
			throw new UnAuthorizedException("User are Not Allowed to See All Orders Details");
		}
		
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orderList);
		response.setMessage("All Orders Details");
		return ResponseEntity.ok(response);
	}
	
	 public List<Long> getAllUserIds() {
		 
		 List<User> allUsers = userRepo.findAll();
		 
		User currUser = currentUser.getUser();
		if(!currUser.getUserRole().equals(Role.ADMIN)) {
			throw new UnAuthorizedException("User are Not Allowed to See All User ID's");
		}
		 
		 List<Long> ids = new ArrayList<>();
		 for(User users : allUsers) {
			 if(users.getUserRole() == Role.CUSTOMER) {
				 ids.add(users.getUserId());
			 }
		 }
		 return ids; 
	 }

	public List<Long> getAllProductIds() {
		User currUser = currentUser.getUser();
		
		if(!currUser.getUserRole().equals(Role.ADMIN)) {
			throw new UnAuthorizedException("User are Not Allowed to See All Product ID's");
		}
		return productRepo.getAllProductIds();
	}

	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		
		List<User> users = userRepo.findAll();
		List<User> getUsers = new ArrayList<>();
		for(User user : users) {
			if(user.getUserRole() == Role.CUSTOMER) {
				getUsers.add(user);
			}
		}
		ApiResponse<List<User>> response = new ApiResponse<>();
		response.setData(users);
		response.setMessage("Users List");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
		
		List<Product> products = productRepo.findAll();
		
		ApiResponse<List<Product>> response = new ApiResponse<>();
		response.setData(products);
		response.setMessage("Products List");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<?>> loginAdmin(LoginDetails details) {
		
		Optional<User> adminExists = userRepo.findByUserEmail(details.getLoginEmail());
		
		if(!adminExists.isPresent()) {
			
			throw new AdminNotFoundException("No Email Found");
		}
		if(adminExists.get().getUserRole()!=Role.ADMIN) {
			throw new UnAuthorizedException("Please Provide Proper Admin Credentials");
		}
		
		User user = adminExists.get();
		UserToken userToken = new UserToken();
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    if (!encoder.matches(details.getLoginPassword(), user.getUserPassword())) {
	        throw new UnAuthorizedException("Invalid credentials.");
	    }
	    String token = UUID.randomUUID().toString();
	    userToken.setUserToken(token);
	    userToken.setGeneratedAt(LocalDateTime.now());
	    userToken.setUser(user);
	    userRepo.save(user);
	    userTokenRepo.save(userToken);
	    
		ApiResponse<User> response  = new ApiResponse<>();
		response.setData(adminExists.get());
		response.setMessage("Login Successful");
		return ResponseEntity.ok(response);
	}
}

