package com.example.repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.CartItem;
import com.example.model.OrderProduct;
import com.example.model.Product;

import jakarta.transaction.Transactional;


@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long>{
	

	@Query("SELECT c FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
	Optional<CartItem> findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

	@Transactional
	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
	void deleteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

	@Query("SELECT c FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
	List<CartItem> findAllByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
	
	@Query("SELECT op FROM CartItem op WHERE op.user.userId = :userId")
	List<CartItem> findByUserId(@Param("userId") Long userId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM CartItem op WHERE op.user.userId = :userId")
	void deleteAllByUser(@Param("userId") Long userId);

	List<CartItem> findAllByProduct(Product product);

}
