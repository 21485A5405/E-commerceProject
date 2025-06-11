package com.example.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Entity.CartItem;


@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long>{
	

	Optional<CartItem> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);



}
