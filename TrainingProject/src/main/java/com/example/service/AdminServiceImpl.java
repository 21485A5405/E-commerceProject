package com.example.service;


import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.controller.ApiResponse;
import com.example.exception.AdminNotFoundException;
import com.example.exception.CustomException;
import com.example.model.Admin;
import com.example.model.LoginDetails;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.User;
import com.example.repo.AdminRepo;
import com.example.repo.OrderRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;

@Service
public class AdminServiceImpl implements AdminService{
	
	private AdminRepo adminRepo;
	private UserRepo userRepo;
	private ProductRepo productRepo;
	private OrderRepo orderRepo;
	
	public AdminServiceImpl(AdminRepo adminRepo, UserRepo userRepo, ProductRepo productRepo, OrderRepo orderRepo) {
		this.adminRepo = adminRepo;
		this.userRepo = userRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
	}
	
	public ResponseEntity<ApiResponse<Admin>> createAdmin(Admin newAdmin) {
		Optional<Admin> exists = adminRepo.findById(newAdmin.getAdminId());
		
		if(!exists.isPresent()) {
			throw new CustomException("Admin Already Exists Please Login");
		}
		adminRepo.save(newAdmin);
		Admin admin = exists.get();
		ApiResponse<Admin> response = new ApiResponse<>();
		response.setData(admin);
		response.setMessage("New Admin Added Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<Admin>> getAdminById(Long adminId) {
		
		Optional<Admin> exists = adminRepo.findById(adminId);
		if(!exists.isPresent()) {
			throw new AdminNotFoundException("Admin Not Found");
		}
		Admin admin = exists.get();
		ApiResponse<Admin> adminFound = new ApiResponse<>();
		adminFound.setData(admin);
		adminFound.setMessage("Admin Details");
		return ResponseEntity.ok(adminFound);
	}

	public ResponseEntity<Admin> updateAdminById(Long adminId, Admin newAdmin) {
		
		Optional<Admin> exists = adminRepo.findById(adminId);
		if(!exists.isPresent()) {
			
			throw new AdminNotFoundException("Admin Not Found");
		}
		
		if(newAdmin.getAdminName() == null) {
			throw new AdminNotFoundException("Admin Name Cannot be Empty");
		}else if(newAdmin.getAdminPermissions() == null) {
			throw new AdminNotFoundException("Admin Permissions Cannot be Empty");
		}else if(newAdmin.getAdminRole() == null) {
			throw new AdminNotFoundException("Admin Role Cannot be Empty");
		}else if(newAdmin.getUserType() == null) {
			throw new AdminNotFoundException("UserType Cannot be Empty");
		}
		
		Admin oldAdmin = exists.get();
		oldAdmin.setAdminName(newAdmin.getAdminName());
		oldAdmin.setAdminPermissions(newAdmin.getAdminPermissions());
		oldAdmin.setAdminRole(newAdmin.getAdminRole());
		oldAdmin.setUserType(newAdmin.getUserType());
		
		adminRepo.save(oldAdmin);
		return ResponseEntity.status(HttpStatus.OK).body(oldAdmin);
	}

	public ResponseEntity<ApiResponse<Admin>> deleteAdminById(Long adminId) {
		
		Optional<Admin> exists = adminRepo.findById(adminId);
		if(!exists.isPresent()) {
			
			throw new AdminNotFoundException("Admin Not Found");
		}
		
		adminRepo.deleteById(adminId);
		Admin admin = exists.get();
		ApiResponse<Admin> response = new ApiResponse<>();
		response.setData(admin);
		response.setMessage("Admin Deleted Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<Admin>>> getAllAdmins() {
		List<Admin> list = adminRepo.findAll();
		ApiResponse<List<Admin>> response = new ApiResponse<>();
		response.setMessage("List Of Admins");
		response.setData(list);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders() {
		List<OrderProduct> orderList = orderRepo.findAll();
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orderList);
		response.setMessage("All Orders Details");
		return ResponseEntity.ok(response);
	}
	
	 public List<Long> getAllUserIds() {
		 
		 return userRepo.getAllUserIds();
		 
	 }

	public List<Long> getAllProductIds() {
		return productRepo.getAllProductIds();
	}

	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		
		List<User> users = userRepo.findAll();
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

	@Override
	public ResponseEntity<ApiResponse<?>> loginAdmin(LoginDetails details) {
		
//		Optional<Admin> exists = adminRepo.findById((long) 10);
		return null;
	}

	
}

