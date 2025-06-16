package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private AdminService adminService;
	
	
	@PostMapping("/addadmin")
	public Admin adminData(@RequestBody Admin admin) {
		
		return adminService.createAdmin(admin);
	}
	
	@GetMapping("/getadminbyid/{adminId}")
	public Optional<Admin> getAdmin(@PathVariable Long adminId) {
		
		return adminService.getAdminById(adminId);
	}
	
	@GetMapping("/getall")
	public List<Admin> getAdmin(@RequestBody Admin admin) {
		return adminService.getAllAdmins(admin);
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
