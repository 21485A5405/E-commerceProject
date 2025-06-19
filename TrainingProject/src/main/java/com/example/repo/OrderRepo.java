package com.example.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.model.OrderProduct;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepo extends JpaRepository<OrderProduct, Long>{
	
	Optional<OrderProduct> findByProduct_ProductId (Long productId);

	Optional<OrderProduct> findByUser_UserIdAndProduct_ProductId (Long userId, Long productId);
	
	List<OrderProduct> findByUser_UserId(Long userId);
	
	List<OrderProduct> findAllByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

	@Transactional
	@Modifying
	void deleteAllByUser_UserId(Long userId);
	
	@Transactional
	@Modifying
	void deleteByUser_UserIdAndProduct_ProductId(Long userId, Long productId);
	
	List<OrderProduct> findAllByOrderStatusIgnoreCase(String status);
	
	List<OrderProduct> findAllByPaymentStatusIgnoreCase(String paymentStatus);

}
