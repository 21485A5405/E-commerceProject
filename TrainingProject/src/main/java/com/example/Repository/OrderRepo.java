package com.example.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Entity.OrderProduct;

@Repository
public interface OrderRepo extends JpaRepository<OrderProduct, Long>{
	
	Optional<OrderProduct> findByProduct_ProductId (Long productId);

	List<OrderProduct> findByUser_UserId(Long userId);

}
