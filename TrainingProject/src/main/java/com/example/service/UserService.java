package com.example.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.dto.DisplayUser;
import com.example.dto.LoginDetails;
import com.example.dto.LoginDisplay;
import com.example.dto.RegisterUser;
import com.example.dto.UpdateUser;
import com.example.enums.AdminPermissions;
import com.example.model.Address;
import com.example.model.PaymentInfo;
import com.example.model.PaymentMethod;
import com.example.model.User;

public interface UserService {
	
	public ResponseEntity<ApiResponse<DisplayUser>> saveUser(RegisterUser user);

	public ResponseEntity<ApiResponse<UpdateUser>> updateUserById(Long userId, UpdateUser user);

	public ResponseEntity<ApiResponse<User>> getUserById(Long userId);

	public ResponseEntity<ApiResponse<User>> deleteUserById(Long userId);
	
	public ResponseEntity<ApiResponse<User>> changeUserPassword(String eMail, String currPassword, String newPassword);

	public ResponseEntity<LoginDisplay> loginUser(LoginDetails details);

	public ResponseEntity<ApiResponse<User>> updateUserRole(Set<AdminPermissions> permissions, Long userId);

	public ResponseEntity<String> logOut();

	public ResponseEntity<String> addAddress(Address address);

	public ResponseEntity<ApiResponse<String>> addPayment(Long userId, PaymentInfo paymentDetails);

	public List<Address> getAddress(Long userId);

	public ResponseEntity<String> updateAddress(Long addressId, String address);

	public List<PaymentMethod> getPayment(Long userId);

	public List<PaymentInfo> getPayments(Long userId);

}
