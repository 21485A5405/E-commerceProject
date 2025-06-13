package com.example.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Entity.Admin;
import com.example.Service.AdminService;

@RestController
@RequestMapping("/create/admins")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	
	@PostMapping
	public Admin adminData(@RequestBody Admin admin) {
		
		return adminService.createAdmin(admin);
		
	}

}
