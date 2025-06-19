package com.example.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Admin;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long>{
	
	
	

}
