package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.Admin;
import com.example.model.Product;
import com.example.model.User;

public interface AdminService {
	
	public Admin createAdmin(Admin admin) ;

	public Optional<Admin> getAdminById(Long adminId);

	public String updateAdminById(Long adminId, Admin newAdmin);

	public String deleteAdminById(Long adminId);

	public List<Admin> getAllAdmins();

	 List<Long> getAllUserIds();

	public List<Long> getAllProductIds();

	public List<User> getAllUsers();

	public List<Product> getAllProducts();

}
