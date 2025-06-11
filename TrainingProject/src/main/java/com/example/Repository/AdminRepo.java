package com.example.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.example.Entity.Admin;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long>{
	
//	@Query("insert into admin (admin_id, admin_name, admin_permissions, admin_role) values(?, ?, ?, ?)")
//	public Admin createAdmin(Admin admin);

}
