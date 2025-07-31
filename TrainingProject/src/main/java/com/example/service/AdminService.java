package com.example.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.dto.LoginDetails;
import com.example.dto.LoginDisplay;
import com.example.dto.RegisterAdmin;
import com.example.dto.UpdateUser;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.User;

public interface AdminService {
	
	public ResponseEntity<ApiResponse<User>> createAdmin(RegisterAdmin admin) ;

	public ResponseEntity<ApiResponse<User>> getAdminById(Long adminId);

	public ResponseEntity<ApiResponse<UpdateUser>> updateAdminById(Long adminId, UpdateUser newAdmin);

	public ResponseEntity<ApiResponse<User>> deleteAdminById(Long adminId);

	public ResponseEntity<ApiResponse<List<User>>> getAllAdmins();

	List<Long> getAllUserIds();

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders();

	public List<Long> getAllProductIds();

	public ResponseEntity<ApiResponse<List<User>>> getAllUsers();

	public ResponseEntity<ApiResponse<List<Product>>> getAllProducts();

	public ResponseEntity<LoginDisplay> loginAdmin(LoginDetails details);

	public ResponseEntity<String> logOut();
	
	public ResponseEntity<UpdateUser> getDetails(Long userId);

}
