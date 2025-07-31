package com.example.controller;

import java.util.ArrayList;
import java.util.Collections;
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

import com.example.dto.RegisterAdmin;
import com.example.customannotations.ForOrders;
import com.example.customannotations.ForProduct;
import com.example.customannotations.ForUser;
import com.example.dto.LoginDetails;
import com.example.dto.LoginDisplay;
import com.example.dto.UpdateUser;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.User;
import com.example.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admins")
public class AdminController {
	

	private AdminService adminService;
	
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}
	
	@PostMapping("/register-admin")
	public ResponseEntity<ApiResponse<User>> RegisterAdmin(@Valid @RequestBody RegisterAdmin admin) {
		return adminService.createAdmin(admin);
	}
	
	@GetMapping("/get-adminbyid/{adminId}")
	public ResponseEntity<ApiResponse<User>> getAdmin(@PathVariable Long adminId) {
		return adminService.getAdminById(adminId);
	}
	
	@GetMapping("/get-all-admins")
	@ForUser(validPermissions = {AdminPermissions.Manager},requiredRole = Role.ADMIN, isSelfUser = false)
	public ResponseEntity<ApiResponse<List<User>>> getAdmin() {
		return adminService.getAllAdmins();
	}
	
	@GetMapping("/get-all-usersbyid")
	@ForUser(validPermissions = {AdminPermissions.Manager,AdminPermissions.User_Manager},requiredRole = Role.ADMIN, isSelfUser = false)
	public List<Long> getUsers() {
		return adminService.getAllUserIds();
	}
	
	@GetMapping("/get-all-products")
	@ForProduct(validPermissions = {AdminPermissions.Manager,AdminPermissions.User_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
		return adminService.getAllProducts();
	}
	
	@GetMapping("/get-all-users")
	@ForUser(validPermissions = {AdminPermissions.Manager,AdminPermissions.User_Manager},requiredRole = Role.ADMIN, isSelfUser = false)
	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		return adminService.getAllUsers();
	}
	
	@GetMapping("/get-all-productsbyid")
	@ForProduct(validPermissions = {AdminPermissions.Manager,AdminPermissions.Product_Manager},requiredRole = Role.ADMIN)
	public List<Long> getProducts() {
		return adminService.getAllProductIds();
	}
	
	@PutMapping("/update-admin/{adminId}")
	public ResponseEntity<ApiResponse<UpdateUser>> updateAdmin(@PathVariable Long adminId, @RequestBody UpdateUser newAdmin) {
		return adminService.updateAdminById(adminId, newAdmin);
	}

	@DeleteMapping("/delete-adminbyid/{adminId}")
	public ResponseEntity<ApiResponse<User>> deleteAdmin(@PathVariable Long adminId) {
		return adminService.deleteAdminById(adminId);
	}
	
	@GetMapping("/get-all-orders")
	@ForOrders(validPermissions = {AdminPermissions.Manager,AdminPermissions.Order_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAll() {
		return adminService.getAllOrders();
	}
	
	@PostMapping("/login-admin")
	public ResponseEntity<LoginDisplay> login(@RequestBody LoginDetails details) {
		return adminService.loginAdmin(details);
	}
	
	@GetMapping("/get-details/{userId}")
	public ResponseEntity<UpdateUser> getDetails(@PathVariable Long userId) {
		return adminService.getDetails(userId);
	}
	
	@DeleteMapping("/logout-admin")
	public ResponseEntity<String> logOut() {
		return adminService.logOut();
	}
}
