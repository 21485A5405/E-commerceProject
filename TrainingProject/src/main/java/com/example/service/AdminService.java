package com.example.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.model.Admin;
import com.example.model.LoginDetails;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.User;

public interface AdminService {
	
	public ResponseEntity<ApiResponse<Admin>> createAdmin(Admin admin) ;

	public ResponseEntity<ApiResponse<Admin>> getAdminById(Long adminId);

	public ResponseEntity<Admin> updateAdminById(Long adminId, Admin newAdmin);

	public ResponseEntity<ApiResponse<Admin>> deleteAdminById(Long adminId);

	public ResponseEntity<ApiResponse<List<Admin>>> getAllAdmins();

	List<Long> getAllUserIds();
	
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders();

	public List<Long> getAllProductIds();

	public ResponseEntity<ApiResponse<List<User>>> getAllUsers();

	public ResponseEntity<ApiResponse<List<Product>>> getAllProducts();

	public ResponseEntity<ApiResponse<?>> loginAdmin(LoginDetails details);

}
