package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.model.Admin;

public interface AdminService {
	
	public Admin createAdmin(Admin admin) ;

	public Optional<Admin> getAdminById(Long adminId);

	public String updateAdminById(Long adminId, Admin newAdmin);

	public String deleteAdminById(Long adminId);

	public List<Admin> getAllAdmins();

	 List<Long> getAllUserIds();

	public List<Long> getAllProductIds();

}
