package com.example.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Address;

import jakarta.transaction.Transactional;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {

	 	@Modifying
	    @Transactional
	    @Query("DELETE FROM Address o WHERE o.user.userId = :userId")
	    void deleteAllByUserId(@Param("userId") Long userId);
	 	
	 	List<Address> findAllByUser_UserId(Long userId);
}
