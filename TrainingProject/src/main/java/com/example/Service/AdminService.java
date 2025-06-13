package com.example.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.Admin;
import com.example.Repository.AdminRepo;

@Service
public class AdminService{
	
	@Autowired
	private AdminRepo adminRepo;
	
	public Admin createAdmin(Admin admin) {
		return adminRepo.save(admin);
	}
	
}

