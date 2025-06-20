package com.example.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.exceptionfile.AdminNotFoundException;
import com.example.exceptionfile.UserNotFoundException;
import com.example.model.Admin;
import com.example.model.Product;
import com.example.model.User;
import com.example.repo.AdminRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;

@Service
public class AdminServiceImpl implements AdminService{
	
	private AdminRepo adminRepo;
	private UserRepo userRepo;
	private ProductRepo productRepo;
	
	public AdminServiceImpl(AdminRepo adminRepo, UserRepo userRepo, ProductRepo productRepo) {
		this.adminRepo = adminRepo;
		this.userRepo = userRepo;
		this.productRepo = productRepo;
	}
	
	public Admin createAdmin(Admin admin) {
		return adminRepo.save(admin);
	}

	public Optional<Admin> getAdminById(Long adminId) {
		
		Optional<Admin> exists = adminRepo.findById(adminId);
		if(!exists.isPresent()) {
			throw new AdminNotFoundException("Admin Not Found");
		}
		return exists;
	}

	public String updateAdminById(Long adminId, Admin newAdmin) {
		
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
		return "New Admin Details Updated Successfully";
	}

	public String deleteAdminById(Long adminId) {
		
		Optional<Admin> exists = adminRepo.findById(adminId);
		if(!exists.isPresent()) {
			
			throw new AdminNotFoundException("Admin Not Found");
		}
		
		adminRepo.deleteById(adminId);
		return "Admin Deleted Successfully";
	}

	public List<Admin> getAllAdmins() {
		return adminRepo.findAll();
	}

	 public List<Long> getAllUserIds() {
		 
		 return userRepo.getAllUserIds();
		 
	 }

	public List<Long> getAllProductIds() {
		return productRepo.getAllProductIds();
	}

	public List<User> getAllUsers() {
		
		return userRepo.findAll();
	}

	public List<Product> getAllProducts() {
		return productRepo.findAll();
	}

	
}

