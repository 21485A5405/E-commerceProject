package com.example.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.advicemethods.*;
import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.dto.LoginDetails;
import com.example.dto.RegisterUser;
import com.example.dto.UpdateUser;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.exception.CustomException;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.Address;
import com.example.model.PaymentInfo;
import com.example.model.User;
import com.example.model.UserToken;
import com.example.repo.*;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{

    private final OrderItemRepo orderItemRepo;
	private CartItemRepo cartItemRepo;
	private OrderRepo orderRepo;
	private UserRepo userRepo;
	private CurrentUser currentUser;
	private UserTokenRepo userTokenRepo;
	private AddressRepo addressRepo;
	
	public UserServiceImpl(CartItemRepo cartItemRepo, OrderRepo orderRepo, AddressRepo addressRepo, UserRepo userRepo, UserTokenRepo userTokenRepo, CurrentUser currentUser, OrderItemRepo orderItemRepo) {
		this.cartItemRepo = cartItemRepo;
		this.orderRepo = orderRepo;
		this.userRepo = userRepo;
		this.currentUser = currentUser;
		this.userTokenRepo = userTokenRepo;
		this.addressRepo = addressRepo;
		this.orderItemRepo = orderItemRepo;
		
	}
	
	public ResponseEntity<ApiResponse<User>> saveUser(RegisterUser user) {
		Optional<User> exists = userRepo.findByUserEmail(user.getUserEmail());
		if(exists.isPresent()) {
			throw new CustomException("User Already Exists Please Login");
		}
		User newUser = new User();

		if(user.getUserName().isBlank()) {
			throw new CustomException("UserName Cannot be Empty");
		}else if(user.getUserEmail().isBlank()) {
			throw new CustomException("UserEmail Cannot be Empty");
		}else if(user.getUserPassword().isBlank() || user.getUserPassword().length()<=5) {
			throw new CustomException("UserPassword Cannot be Empty / less Than % Characters");
		}
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String hashedPassword = encoder.encode(user.getUserPassword());
			newUser.setUserEmail(user.getUserEmail());
			newUser.setUserName(user.getUserName());
			newUser.setUserPassword(hashedPassword);
			newUser.setUserRole(Role.CUSTOMER);
			userRepo.save(newUser);

		ApiResponse<User> response = new ApiResponse<>();
		response.setData(newUser);
		response.setMessage("New User Added Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<User>> updateUserById(Long userId, UpdateUser newUser) {
		
		Optional<User> u = userRepo.findById(userId);
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		if(currUser.getUserId() != userId) {
			throw new UnAuthorizedException("You Are Not Allowed To Update Another User Details");
		}
		
		if(newUser.getUserName() == null) {
			throw new CustomException("UserName Cannot be Empty");
		}else if(newUser.getUserEmail() == null) {
			throw new CustomException("UserEmail Cannot be Empty");
		}else if(newUser.getShippingAddress() == null) {
			throw new CustomException("Shipping Address Cannot be Empty");
		}else if(newUser.getPaymentDetails() == null) {
			throw new CustomException("Payment Details Cannot be Empty");
		}
		User oldUser = u.get();
		
		oldUser.setUserName(newUser.getUserName());
		oldUser.setUserEmail(newUser.getUserEmail());
		
		 List<Address> existingAddresses = newUser.getShippingAddress();
		    existingAddresses.clear();
		    for (Address address : newUser.getShippingAddress()) {
		        address.setUser(oldUser); // maintain bidirectional link
		        existingAddresses.add(address);
		    }
		oldUser.setPaymentDetails(newUser.getPaymentDetails());

		userRepo.save(oldUser);
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(oldUser);
		response.setMessage("User Updated Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<User>> getUserById(Long userId) {
		
		Optional<User> exists = userRepo.findById(userId);
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(!exists.isPresent()) {
			
			throw new UserNotFoundException("User Not Found");
		}
		if(currUser.getUserId()!= userId && currUser.getUserRole() != Role.ADMIN) {
			throw new UnAuthorizedException("Not Allowed to Get Another User Details");
		}
		boolean isManager = IsAuthorized.isUserManager(currUser.getUserPermissions());
		boolean isUserManager = IsAuthorized.isManager(currUser.getUserPermissions());
		boolean isAdmin = IsAuthorized.isAdmin(currUser.getUserRole());
		
		if ( isAdmin && !(isUserManager) || isManager ) {
				throw new UnAuthorizedException("Only Manager and User_Manager have Right To User Details");
			}
		
		if(exists.get().getUserRole() == Role.ADMIN) {
			throw new UnAuthorizedException("User "+userId+ " is Not User");
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
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(!exists.isPresent()) {
			
			throw new UserNotFoundException("User Not Found");
		}
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("You Are Not Allowed To Delete Another User");
		}
		
		cartItemRepo.deleteAllByUser(userId);
		orderItemRepo.deleteAllByOrderId(orderRepo.findById(userId).get().getOrderId());
		orderRepo.deleteAllByUserId(userId);
		userTokenRepo.deleteByUserId(userId);
		addressRepo.deleteAllByUserId(userId);
		userRepo.deleteById(userId);
		ApiResponse<User> response = new ApiResponse<>();
		response.setMessage("User with Role "+exists.get().getUserRole()+" Deleted Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<User>> changeUserPassword(String eMail, String newPassword) {
		
	    Optional<User> exists = userRepo.findByUserEmail(eMail);
	    
	    User currUser = currentUser.getUser();
	    if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
	    if (!exists.isPresent()) {
	        throw new UnAuthorizedException("No User found with this Email");
	    }
	    if (newPassword.isBlank() || newPassword.length()<=5) {
	        throw new CustomException("New password cannot be empty or Less Than 5 Characters");
	    }
		if(currUser.getUserId()!= exists.get().getUserId()) {
			throw new UnAuthorizedException("You Are Not Allowed to Change Another User Password");
		}
	    
	    User user = exists.get();
	    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    user.setUserPassword(encoder.encode(newPassword));
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
		if(exists.get().getUserRole() != Role.CUSTOMER) {
			throw new UnAuthorizedException("Please Provide User Credentials");
		}
		if(userTokenRepo.findByUser(exists.get()) !=null) {
			throw new CustomException("User Already Logged In");
		}
		User currUser = currentUser.getUser();
		User user = exists.get();
		if(currUser != null && currUser.getUserId() == user.getUserId()) {
			throw new CustomException("You Already In Current Session");
		}
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
	public ResponseEntity<ApiResponse<User>> updateUserRole(Set<AdminPermissions> permissions, Long userId) {
		
		Optional<User> exists = userRepo.findById(userId);
		if(!exists.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		exists.get().setUserRole(Role.ADMIN);
		exists.get().setUserPermissions(new HashSet<>(permissions));
		
		userRepo.save(exists.get());
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(exists.get());
		response.setMessage("User Role Updated Successfully");
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<ApiResponse<User>> logOut() {
		
		User currUser = currentUser.getUser();
		userTokenRepo.deleteByUserId(currUser.getUserId());
		ApiResponse<User> response = new ApiResponse<>();
		response.setData(currUser);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<String> addAddress(Address address) {
		
		User newUser = currentUser.getUser();
		
		address.setUser(newUser);
	    newUser.getShippingAddress().add(address);
	    newUser.setShippingAddress(newUser.getShippingAddress());
	    addressRepo.save(address);
		return ResponseEntity.ok("Address Added Successfully");
		
	}

	public ResponseEntity<String> addPayment(PaymentInfo paymentDetails) {
		
		User newUser = currentUser.getUser();

		newUser.getPaymentDetails().add(paymentDetails);
	    userRepo.save(newUser);
		return ResponseEntity.ok("Payment Added Successfully");
	}
	
}