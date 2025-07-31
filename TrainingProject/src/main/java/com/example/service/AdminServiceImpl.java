package com.example.service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.advicemethods.IsAuthorized;
import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.dto.LoginDetails;
import com.example.dto.LoginDisplay;
import com.example.dto.RegisterAdmin;
import com.example.dto.UpdateUser;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.exception.AdminNotFoundException;
import com.example.exception.CustomException;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.Address;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.User;
import com.example.model.UserToken;
import com.example.repo.OrderRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;
import com.example.repo.UserTokenRepo;

import jakarta.transaction.Transactional;

@Service
public class AdminServiceImpl implements AdminService{
	
	private UserRepo userRepo;
	private ProductRepo productRepo;
	private OrderRepo orderRepo;
	private CurrentUser currentUser;
	private UserTokenRepo userTokenRepo;
	
	public AdminServiceImpl(UserRepo userRepo, ProductRepo productRepo, OrderRepo orderRepo, 
						UserTokenRepo userTokenRepo, CurrentUser currentUser) {
		this.userRepo = userRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
		this.currentUser = currentUser;
		this.userTokenRepo = userTokenRepo;
	}
	
	public ResponseEntity<ApiResponse<User>> createAdmin(RegisterAdmin newAdmin) {
			Optional<User> exists = userRepo.findByUserEmail(newAdmin.getUserEmail());
			if(exists.isPresent()) {
				throw new CustomException("Admin Already Exists Please Login");
			}
			if(newAdmin.getUserName() == null) {
				throw new CustomException("UserName Cannot be Empty");
			}else if(newAdmin.getUserEmail() == null) {
				throw new CustomException("UserEmail Cannot be Empty");
			}
			User newUser = new User();
		
			newUser.setUserName(newAdmin.getUserName());
			newUser.setUserEmail(newAdmin.getUserEmail());
			newUser.setUserRole(Role.ADMIN);
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String hashedPassword = encoder.encode(newAdmin.getUserPassword());
			newUser.setUserPassword(hashedPassword);
			userRepo.save(newUser);

			ApiResponse<User> response = new ApiResponse<>();
			response.setData(newUser);
			response.setMessage("New Admin Added Successfully");
			return ResponseEntity.ok(response);
		}

	public ResponseEntity<ApiResponse<User>> getAdminById(Long adminId) {

		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<User> exists = userRepo.findById(adminId);
		if(!exists.isPresent()) {
			throw new AdminNotFoundException("Admin Not Found");
		}
		if(currUser.getUserRole()!=Role.ADMIN) {
			throw new CustomException("User Dont Have Access to See Admin Details");
		}
		
		boolean isManager = IsAuthorized.isManager(currUser.getUserPermissions());
		boolean isAdmin = IsAuthorized.isAdmin(currUser.getUserRole());
		
	    if (!currUser.getUserId().equals(adminId) && !isManager) {
	        throw new UnAuthorizedException("Not Authorized to See Another Admin's Details");
	    }if(!isAdmin) {
			throw new CustomException("User "+adminId +" is Not An Admin");
		}
			User admin = exists.get();
			ApiResponse<User> adminFound = new ApiResponse<>();
			adminFound.setData(admin);
			adminFound.setMessage("Admin Details");
			return ResponseEntity.ok(adminFound);
	}

	@Transactional
	public ResponseEntity<ApiResponse<UpdateUser>> updateAdminById(Long adminId, UpdateUser newAdmin) {
		
		Optional<User> u = userRepo.findById(adminId);
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(!u.isPresent()) {
			throw new UserNotFoundException("Admin Not Found");
		}
		if(currUser.getUserRole() !=Role.ADMIN) {
			throw new UnAuthorizedException("User Not Allowed To Update Another Admin Details");
		}
		if(currUser.getUserId() != adminId) {
			throw new UnAuthorizedException("You Are Not Allowed To Update Another Admin Details");
		}
		
		if(newAdmin.getUserName().isBlank()) {
			throw new CustomException("UserName Cannot be Empty");
		}else if(newAdmin.getUserEmail().isBlank()) {
			throw new CustomException("UserEmail Cannot be Empty");
		}
		User admin = u.get();
		
		admin.setUserName(newAdmin.getUserName());
		admin.setUserEmail(newAdmin.getUserEmail());
		
//		 List<Address> existingAddresses = admin.getShippingAddress();
//		    existingAddresses.clear();
//		    for (Address address : newAdmin.getShippingAddress()) {
//		        address.setUser(admin); // maintain bidirectional link
//		        existingAddresses.add(address);
//		    }
//		admin.setPaymentDetails(newAdmin.getPaymentDetails());
		
		userRepo.save(admin);
		UpdateUser res = new UpdateUser(admin);
		ApiResponse<UpdateUser> response = new ApiResponse<>();
		response.setData(res);
		response.setMessage("Admin Updated Successfully");
		return ResponseEntity.ok(response);
	}
	
	@Transactional
	public ResponseEntity<ApiResponse<User>> deleteAdminById(Long adminId) {

		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<User> exists = userRepo.findById(adminId);
		if(!exists.isPresent()) {
			throw new AdminNotFoundException("Admin Not Found");
		}
		if(exists.get().getUserRole()!=Role.ADMIN) {
			throw new AdminNotFoundException("User Not Allowed To Delete Admin Details");
		}
	    if (!currUser.getUserId().equals(adminId)) {
	        throw new UnAuthorizedException("Not Authorized to Delete Another Admin's Details");
	    }
		else {
			
			userTokenRepo.deleteByUserId(adminId);
			userRepo.deleteById(adminId);
			ApiResponse<User> response = new ApiResponse<>();
			response.setMessage("Admin Deleted Successfully");
			return ResponseEntity.ok(response);
		}
	}

	public ResponseEntity<ApiResponse<List<User>>> getAllAdmins() {
		
		List<User> list = userRepo.findAll();
		List<User> admins = new ArrayList<>();
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		for(User users :list) {
			if(users.getUserRole() == Role.ADMIN) {
				admins.add(users);
			}
		}
		ApiResponse<List<User>> response = new ApiResponse<>();
		response.setMessage("List Of Admins");
		response.setData(admins);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders() {
		List<OrderProduct> orderList = orderRepo.findAll();
	
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserRole()!=Role.ADMIN) {
			throw new UnAuthorizedException("User are Not Allowed to See All Orders Details");
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orderList);
		response.setMessage("All Orders Details");
		return ResponseEntity.ok(response);
	}
	
	 public List<Long> getAllUserIds() {
		 
		 List<User> allUsers = userRepo.findAll();
		
		 List<Long> ids = new ArrayList<>();
		 for(User users : allUsers) {
			 if(users.getUserRole() == Role.CUSTOMER) {
				 ids.add(users.getUserId());
			 }
		 }
		 return ids; 
	 }
	 
	public List<Long> getAllProductIds() {
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
		response.setData(getUsers);
		response.setMessage("Users List");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {	
		List<Product> products = productRepo.findAll();
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		ApiResponse<List<Product>> response = new ApiResponse<>();
		response.setData(products);
		response.setMessage("Products List");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<LoginDisplay> loginAdmin(LoginDetails details) {
		
		Optional<User> adminExists = userRepo.findByUserEmail(details.getLoginEmail());
		
		if(!adminExists.isPresent()) {
			
			throw new AdminNotFoundException("Admin Not Found");
		}
		if(adminExists.get().getUserRole()!=Role.ADMIN) {
			throw new UnAuthorizedException(adminExists.get().getUserId()+" is Not Admin Please Provide Admin Details");
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
	    userTokenRepo.save(userToken);
	    LoginDisplay response = new LoginDisplay(userToken);
	    response.setUserId(userToken.getUser().getUserId());
	    response.setUserToken(userToken.getUserToken());
	    return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<String> logOut() {
		
		User user = currentUser.getUser();
		userTokenRepo.deleteByUserId(user.getUserId());
		return ResponseEntity.ok("LogOut Successfully");
	}

	public ResponseEntity<UpdateUser> getDetails(Long userId) {
		Optional<User> exists = userRepo.findById(userId);
		if(!exists.isPresent()) {
			throw new UserNotFoundException("");
		}
		UpdateUser details = new UpdateUser(exists.get());
		return ResponseEntity.ok(details);
	}
	
	
}

