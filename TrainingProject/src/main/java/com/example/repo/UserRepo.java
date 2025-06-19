package com.example.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.model.User;

@Repository

public interface UserRepo extends JpaRepository<User, Long> {
	
	Optional<User> findByUserEmail(String eMail);
	
	@Query("SELECT u.userId FROM User u")
    List<Long> getAllUserIds();
}
