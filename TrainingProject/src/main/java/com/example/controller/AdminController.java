package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.LoginDetails;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.User;
import com.example.service.AdminService;
import com.example.service.OrderService;

@RestController
@RequestMapping("/admins")
public class AdminController {
	

	private AdminService adminService;
	private OrderService orderService;
	
	public AdminController(AdminService adminService, OrderService orderService) {
		this.adminService = adminService;
		this.orderService = orderService;
	}
	
//	@PostMapping("/register-admin")
//	public ResponseEntity<ApiResponse<User>> adminData(@RequestBody User admin) {
//		return adminService.createAdmin(admin);
//	}
	
	@GetMapping("/get-admin-by-id/{adminId}")
	public ResponseEntity<ApiResponse<User>> getAdmin(@PathVariable Long adminId) {
		return adminService.getAdminById(adminId);
	}
	
	@GetMapping("/get-all-admins")
	public ResponseEntity<ApiResponse<List<User>>> getAdmin() {
		return adminService.getAllAdmins();
	}
	
	@GetMapping("/get-all-user-id")
	public List<Long> getUsers() {
		return adminService.getAllUserIds();
	}
	
	@GetMapping("/get-all-products")
	public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
		return adminService.getAllProducts();
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		return adminService.getAllUsers();
	}
	
	@GetMapping("/get-all-product-id")
	public List<Long> getProducts() {
		return adminService.getAllProductIds();
	}
	@PutMapping("/update-admin/{adminId}")
	public ResponseEntity<User> updateAdmin(@PathVariable Long adminId, @RequestBody User newAdmin) {
		return adminService.updateAdminById(adminId, newAdmin);
	}

	@DeleteMapping("/delete-admin-by-id/{adminId}")
	public ResponseEntity<ApiResponse<User>> deleteAdmin(@PathVariable Long adminId) {
		return adminService.deleteAdminById(adminId);
	}
	
	@GetMapping("/getall-orders")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAll() {
		return adminService.getAllOrders();
	}
	
	@PostMapping("/login-admin")
	public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDetails details) {
		return adminService.loginAdmin(details);
	}
}