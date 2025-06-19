package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.exceptionfile.CustomException;
import com.example.exceptionfile.ProductNotFoundException;
import com.example.exceptionfile.UserNotFoundException;
import com.example.model.CartItem;
import com.example.model.OrderProduct;
import com.example.model.Product;
import com.example.model.User;
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
	
	public OrderServiceImpl(UserRepo userRepo, CartItemRepo cartItemRepo, ProductRepo productRepo, OrderRepo orderRepo) {
		this.userRepo = userRepo;
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
	}

	public String placeOrder(Long userId, Long productId) {

	    Optional<User> findUser = userRepo.findById(userId);
	    Optional<Product> findProduct = productRepo.findById(productId);
	    Optional<CartItem> cart = cartItemRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);
	    
	    
	    if (!findUser.isPresent()) {
	        throw new UserNotFoundException("User Not Found");
	    }
	    if (!findProduct.isPresent()) {
	        throw new ProductNotFoundException("Product Not Available");
	    }
	    if (!cart.isPresent()) {
	        throw new ProductNotFoundException("Please Add Product into Cart to place Order");
	    }else {
	    	 CartItem cartItem = cart.get();
	 	    User user = findUser.get();
	 	    Product product = findProduct.get();
	 	    OrderProduct orderProduct = new OrderProduct();
	 	    orderProduct.setProduct(product);
	 	    orderProduct.setUser(user);
	 	    orderProduct.setShippingAddress(user.getShippingAddress());
	 	    orderProduct.setTotalPrice(cartItem.getTotalPrice());
	 	    orderProduct.setOrderQuantity(cartItem.getProductQuantity());
	 	    orderProduct.setOrderStatus("Order Placed");
	 	    orderProduct.setPaymentStatus("Payment Done");
	 	    product.setProductQuantity(product.getProductQuantity()-cartItem.getProductQuantity());
	 	    orderRepo.save(orderProduct);
	 	    cartItemRepo.delete(cartItem);
	    }
	    
	    return "Order Placed Successfully";
	}

	public List<OrderProduct> getOrderByUser(Long userId) {
		
		List<OrderProduct> o = orderRepo.findByUser_UserId(userId);
		
		if(o.isEmpty()) {
			throw new UserNotFoundException("No Order Details Found with Given User ID");
		}
		
		return o;
	}

	@Transactional
	public String cancelOrder(Long userId, Long productId) {
		
		Optional<OrderProduct> o = orderRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);
		
		Optional<Product> p = productRepo.findById(productId);
		
		if(!o.isPresent()) {
			throw new ProductNotFoundException("Product "+productId+" Not Placed By User "+userId);
		}
		orderRepo.deleteByUser_UserIdAndProduct_ProductId(userId, productId);
		
		OrderProduct order = o.get();
		Product product = p.get();
		product.setProductQuantity(product.getProductQuantity()+order.getOrderQuantity());
		
		return "Order Cancelled Successfully";
	}

	public List<OrderProduct> getByUserIdAndProductId(Long userId, Long productId) {
		
		List<OrderProduct> o = orderRepo.findAllByUser_UserIdAndProduct_ProductId(userId, productId);
		
		if(o.isEmpty()) {
			throw new CustomException("Order Not Found With This UserID "+userId+" and ProductID "+productId);
		}
		return o;
	}
	
	public List<OrderProduct> getAllOrders() {
		return orderRepo.findAll();
	}

	public List<OrderProduct> getOrderStatus(String status) {
		
		List<OrderProduct> orders = orderRepo.findAllByOrderStatusIgnoreCase(status);
		if(orders.isEmpty()) {
			throw new CustomException("No Order Found");
		}
		return orders;
	}

	public List<OrderProduct> getOrderByPayment(String paymentStatus) {
		
		List<OrderProduct> o = orderRepo.findAllByPaymentStatusIgnoreCase(paymentStatus);
		
		if(o.isEmpty()) {
			throw new CustomException("No Orders Found With "+paymentStatus);
		}
		return o;
	}

}