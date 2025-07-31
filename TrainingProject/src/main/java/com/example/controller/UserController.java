package com.example.controller;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.customannotations.ForUser;
import com.example.dto.DisplayUser;
import com.example.dto.LoginDetails;
import com.example.dto.LoginDisplay;
import com.example.dto.RegisterUser;
import com.example.dto.UpdateUser;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.model.Address;
import com.example.model.PaymentInfo;
import com.example.model.PaymentMethod;
import com.example.model.User;
import com.example.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
		
	}
	
	@PostMapping("/register-user")
	public ResponseEntity<ApiResponse<DisplayUser>> createUser(@Valid @RequestBody RegisterUser user) {
		return userService.saveUser(user);
	}
	
	@PostMapping("/login-user")
	public ResponseEntity<LoginDisplay> loginUser(@RequestBody LoginDetails details) {
		System.out.println(details.toString());
		return userService.loginUser(details);
	}
	@GetMapping("/get-user-by-id/{userId}")
	public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long userId) {
		return userService.getUserById(userId);
	}
	
	@PutMapping("/update-user/{userId}")
	public ResponseEntity<ApiResponse<UpdateUser>> updateUser(@PathVariable Long userId, @RequestBody UpdateUser user) {
		return userService.updateUserById(userId, user);
	}
	
	@PutMapping("/change-password/{eMail}/{currPassword}/{newPassword}")
	public ResponseEntity<ApiResponse<User>> changePassword(@PathVariable String eMail, @PathVariable String currPassword, @PathVariable String newPassword) {
		return userService.changeUserPassword(eMail, currPassword, newPassword);
	}
	
	@DeleteMapping("/delete-user-by-id/{userId}")
	public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable Long userId) {
		return userService.deleteUserById(userId);
		
	}
	
	@PutMapping("/update-user-role/{userId}")
	@ForUser(validPermissions = {AdminPermissions.Manager, AdminPermissions.User_Manager},requiredRole = Role.ADMIN, isSelfUser = false)
	public ResponseEntity<ApiResponse<User>> updateRole(@RequestBody Set<AdminPermissions> permissions, @PathVariable Long userId) {
		
		return userService.updateUserRole(permissions, userId);
	}
	
	@GetMapping("/get-address/{userId}")
	public List<Address> getAddress(@PathVariable Long userId) {
		return userService.getAddress(userId);
	}
	
	@GetMapping("/get-user-payment/{userId}")
	public List<PaymentMethod> getPayment(@PathVariable Long userId) {
		return userService.getPayment(userId);
	}
	
	
	@PutMapping("/edit-address/{addressId}/{address}")
	public ResponseEntity<String> updateAddress(@PathVariable Long addressId, @PathVariable String address) {
		return userService.updateAddress(addressId, address);
	}
	
	@PostMapping("/add-address")
	public ResponseEntity<String> addAddress(@RequestBody Address address) {
		return userService.addAddress(address);
	}
	
	@GetMapping("/get-user-payments/{userId}")
	public List<PaymentInfo> getPayments(@PathVariable Long userId) {
		return userService.getPayments(userId);
	}
	
	
	@GetMapping("/get-payment-methods")
	public ResponseEntity<PaymentMethod[]> getPaymentMethods() {
	    return ResponseEntity.ok(PaymentMethod.values());
	}
	
	@PostMapping("/add-payment/{userId}")
	public ResponseEntity<ApiResponse<String>> addPayment(@PathVariable Long userId, @RequestBody PaymentInfo paymentDetails) {
		return userService.addPayment(userId, paymentDetails);
	}
	
	@DeleteMapping("/logout-user")
	public ResponseEntity<String> logOut() {
		return userService.logOut();
	}
}