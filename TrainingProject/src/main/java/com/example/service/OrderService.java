package com.example.service;

import java.util.List;

import com.example.model.OrderProduct;

public interface OrderService {
	
	public List<OrderProduct> getOrderByUser(Long userId);
	
	public String placeOrder(Long userId, Long productId);

	public String cancelOrder(Long userId, Long productId);

	public List<OrderProduct> getByUserIdAndProductId(Long userId, Long productId);

	public List<OrderProduct> getAllOrders(OrderProduct orderproduct);
	

}
