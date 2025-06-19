package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Admin;
import com.example.service.AdminService;


@RestController
@RequestMapping("/admins")
public class AdminController {
	

	private AdminService adminService;
	
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}
	
	
	@PostMapping("/addadmin")
	public Admin adminData(@RequestBody Admin admin) {
		
		return adminService.createAdmin(admin);
	}
	
	@GetMapping("/getadminbyid/{adminId}")
	public Optional<Admin> getAdmin(@PathVariable Long adminId) {
		
		return adminService.getAdminById(adminId);
	}
	
	@GetMapping("/getalladmins")
	public List<Admin> getAdmin() {
		return adminService.getAllAdmins();
	}
	
	@GetMapping("/getallusers")
	public List<Long> getUsers() {
		return adminService.getAllUserIds();
	}
	
	@GetMapping("/getallproducts")
	public List<Long> getProducts() {
		return adminService.getAllProductIds();
	}
	@PutMapping("/updateadmin/{adminId}")
	public String updateAdmin(@PathVariable Long adminId, @RequestBody Admin newAdmin) {
		
		String message = adminService.updateAdminById(adminId, newAdmin);
		return message;
	}

	@DeleteMapping("/deleteadminbyid/{adminId}")
	public String deleteAdmin(@PathVariable Long adminId) {
		
		String message = adminService.deleteAdminById(adminId);
		return message;
	}

}
