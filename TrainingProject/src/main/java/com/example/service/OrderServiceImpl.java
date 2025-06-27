package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.*;
import com.example.repo.AddressRepo;
import com.example.repo.CartItemRepo;
import com.example.repo.OrderRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService{
	
	
	public OrderRepo orderRepo;
	private UserRepo userRepo;
	private ProductRepo productRepo;
	private CartItemRepo cartItemRepo;
	private AddressRepo addressRepo;
	
	public OrderServiceImpl(UserRepo userRepo, CartItemRepo cartItemRepo, ProductRepo productRepo, OrderRepo orderRepo, AddressRepo addressRepo) {
		this.userRepo = userRepo;
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
		this.addressRepo = addressRepo;
	}

	public ResponseEntity<ApiResponse<OrderProduct>> placeOrder(Long userId, Long productId, int quantity, Long addressId) {
		

	    Optional<User> findUser = userRepo.findById(userId);
	    Optional<Product> findProduct = productRepo.findById(productId);
	    Optional<CartItem> cart = cartItemRepo.findByUserAndProduct(userId, productId);
	    Optional<Address> addressExists = addressRepo.findById(addressId);
	    
	    
	    if (!findUser.isPresent()) {
	        throw new UserNotFoundException("User Not Found");
	    }
	    if (!findProduct.isPresent()) {
	        throw new ProductNotFoundException("Product Not Available");
	    }
	    
	    if (!cart.isPresent()) {
	        throw new ProductNotFoundException("Please Add Product into Cart to place Order");
	    }
	    if(!addressExists.isPresent()) {
	    	throw new CustomException("No Address Found with Address ID" +addressId);
	    }
	    CartItem cartItem = cart.get();
	    if(cartItem.getProductQuantity()<quantity) {
	    	throw new CustomException("Only "+cartItem.getProductQuantity()+" Items Added Into Cart Please Add "+(quantity-cartItem.getProductQuantity())+" items to Place an Order");
	    }
 	    OrderProduct order = new OrderProduct();
 	    OrderItem orderItem = new OrderItem();
	    if(cart.isPresent()) {	 
	 	    User user = findUser.get();
	 	    Product product = findProduct.get();
	 	    order.setUser(user);
	 	    order.setOrderDate(LocalDateTime.now());
	 	    order.setTotalPrice(cartItem.getTotalPrice());
	 	    Address address = addressExists.get();
	 	    order.setShippingAddress(address.getFullAddress());
	 	    order.setOrderStatus(OrderStatus.PROCESSING);
	 	    order.setPaymentStatus(PaymentStatus.PAID);
	 	    order.setTotalPrice(quantity*product.getProductPrice());
	 	    cartItem.setProductQuantity(cartItem.getProductQuantity()-quantity);
	 	    cartItem.setTotalPrice(cartItem.getProductQuantity()*product.getProductPrice());
	 	    if(cartItem.getProductQuantity() == 0) {
	 	    	cartItemRepo.delete(cartItem);
	 	    }
	 	    orderItem.setOrder(order);
	 	    orderItem.setProduct(product);
	 	    orderItem.setQuantity(cartItem.getProductQuantity());
	 	    
	 	    ArrayList<OrderItem> items = new ArrayList<>();
	 	    items.add(orderItem);
	 	    order.setItems(items);
	 	    orderRepo.save(order);
	 	    
	 	    
	    }
	    ApiResponse<OrderProduct> response = new ApiResponse<>();
	    response.setData(order);
	    response.setMessage("Order Placed Successfully");
	    return ResponseEntity.ok(response);
	
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByUser(Long userId) {
		
		List<OrderProduct> orders = orderRepo.findByUser(userId);
		
		if(orders.isEmpty()) {
			throw new UserNotFoundException("No Order Details Found with Given User ID");
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("User "+userId+" Orders Details");
		return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(Long userId, Long productId, int quantity) {

	    Optional<OrderProduct> orderExists = orderRepo.findByUserAndProductAndQuantity(userId, productId, quantity);
	    if (!orderExists.isPresent()) {
	        throw new ProductNotFoundException("No matching order found for user " + userId + " with product " + productId + " and quantity " + quantity);
	    }
	    OrderProduct order = orderExists.get();

	    OrderItem matchingItem = null;
	    for (OrderItem item : order.getItems()) {
	        if (item.getProduct().getProductId().equals(productId) && item.getQuantity() == quantity) {
	            matchingItem = item;
	            break;
	        }
	    }

	    if (matchingItem == null) {
	        throw new ProductNotFoundException("No matching item found in order for product " + productId);
	    }
	    
	    Optional<Product> productExists = productRepo.findById(productId);
	    if (!productExists.isPresent()) {
	        throw new ProductNotFoundException("Product " + productId + " not found");
	    }

	    Product product = productExists.get();
	    product.setProductQuantity(product.getProductQuantity() + matchingItem.getQuantity());
	    productRepo.save(product);
	    orderRepo.delete(order);

	    ApiResponse<OrderProduct> response = new ApiResponse<>();
	    response.setMessage("Order cancelled successfully");
	    response.setData(order);
	    return ResponseEntity.ok(response);
	}


	public ResponseEntity<ApiResponse<List<OrderProduct>>> getByUserIdAndProductId(Long userId, Long productId) {
		
		List<OrderProduct> orders = orderRepo.findAllByUserAndProduct(userId, productId);
		
		if(orders.isEmpty()) {
			throw new ProductNotFoundException("Orders Not Found With This UserID "+userId+" and ProductID "+productId);
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Orders Details");
		return ResponseEntity.ok(response);
	}
	
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders() {
		List<OrderProduct> orderList = orderRepo.findAll();
		
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orderList);
		response.setMessage("All Orders Details");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderStatus(String status) {
		
		List<OrderProduct> orders = orderRepo.findAllByOrderStatus(status);
		if(orders.isEmpty()) {
			throw new CustomException("No Order Found with Order Status "+status);
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Order Status "+status);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByPayment(String paymentStatus) {
		
		List<OrderProduct> orders = orderRepo.findAllByPaymentStatus(paymentStatus);
		
		if(orders.isEmpty()) {
			throw new CustomException("No Orders Found With Payment Status "+paymentStatus);
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Payment Status "+paymentStatus);
		return ResponseEntity.ok(response);
	}

}