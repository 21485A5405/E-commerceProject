package com.example.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Repository;
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

