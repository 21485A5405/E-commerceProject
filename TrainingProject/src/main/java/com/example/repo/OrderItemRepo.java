package com.example.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.OrderItem;

import jakarta.transaction.Transactional;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long>{
	
	@Modifying
	@Transactional
	@Query("DELETE FROM OrderItem oi WHERE oi.product.productId = :productId")
	void deleteAllByProductId(@Param("productId") Long productId);
	  
	@Modifying
	@Transactional
	@Query("DELETE FROM OrderItem o WHERE o.order.orderId = :orderId")
	void deleteAllByOrderId(@Param("orderId") Long orderId);
	
	Optional<OrderItem> findByOrder_OrderId(Long orderId);

}
