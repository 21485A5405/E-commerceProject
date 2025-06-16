package com.example.repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.model.CartItem;

import jakarta.transaction.Transactional;


@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long>{
	

	Optional<CartItem> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

	@Transactional
	@Modifying
	void deleteByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

	List<CartItem> findAllByUser_UserIdAndProduct_ProductId(Long userId, Long productId);


	List<CartItem> findByUser_UserId(Long userId);
	
	@Transactional
	@Modifying
	void deleteAllByUser_UserId(Long userId);
	

}
