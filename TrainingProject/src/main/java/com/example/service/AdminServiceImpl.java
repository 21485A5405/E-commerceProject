package com.example.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.exceptionfile.AdminNotFoundException;
import com.example.model.Admin;
import com.example.repo.AdminRepo;

@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	private AdminRepo adminRepo;
	
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

	public List<Admin> getAllAdmins(Admin admin) {
		return adminRepo.findAll();
	}

	
}

