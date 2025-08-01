package com.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.advicemethods.IsAuthorized;
import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.dto.PlaceOrder;
import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.enums.Role;
import com.example.exception.CustomException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.UnAuthorizedException;
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
	private CurrentUser currentUser;
	
	public OrderServiceImpl(UserRepo userRepo, CurrentUser currentUser, CartItemRepo cartItemRepo, ProductRepo productRepo, OrderRepo orderRepo, AddressRepo addressRepo) {
		this.userRepo = userRepo;
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
		this.addressRepo = addressRepo;
		this.currentUser = currentUser;
	}

	@Transactional
	public ResponseEntity<ApiResponse<OrderProduct>> placeOrder(PlaceOrder orderDetails) {

	    Optional<User> findUser = userRepo.findById(orderDetails.getUserId());
	    Optional<Product> findProduct = productRepo.findById(orderDetails.getProductId());
	    Optional<CartItem> cart = cartItemRepo.findByUserAndProduct(orderDetails.getUserId(), orderDetails.getProductId());
	    Optional<Address> addressExists = addressRepo.findById(orderDetails.getAddressId());

	    User currUser = currentUser.getUser();
	    if (currUser == null) {
	        throw new UnAuthorizedException("Please Login");
	    }
	    if (!findUser.isPresent()) {
	        throw new UserNotFoundException("User Not Found");
	    }
	    if (!currUser.getUserId().equals(orderDetails.getUserId())) {
	        throw new UnAuthorizedException("Not Authorized to Place Order with Another User ID");
	    }
	    
	    if (!findProduct.isPresent()) {
	        throw new ProductNotFoundException("Product Not Available");
	    }
	    if (!cart.isPresent()) {
	        throw new ProductNotFoundException("Please Add Product into Cart to place Order");
	    }
	    if(!addressExists.get().getUser().getUserId().equals(orderDetails.getUserId())) {
	    	throw new CustomException("Address Not Matched");
	    }

	    CartItem cartItem = cart.get();
	    if (cartItem.getProductQuantity() < orderDetails.getQuantity()) {
	        throw new CustomException("Selected Quantity is Greater Than Your Cart Quantity");
	    }

	    List<PaymentInfo> payment = findUser.get().getPaymentDetails();
	    if (payment == null || payment.isEmpty()) {
	        throw new CustomException("Payment Method Cannot be Empty");
	    }
	    boolean isValid = false; 
	    for (PaymentInfo info : payment) {
	    	if (info.getPaymentMethod() == orderDetails.getPaymentType()) { 
	    		isValid = true; 
	    		break; 
	    		} 
	    	} 
	    if (!isValid) { 
	    	throw new UnAuthorizedException("Selected Payment Method Not Available. Available: " 
	    					+ findUser.get().displayPayments()); 
	    	}
	    // Create order
	    OrderProduct order = new OrderProduct();
	    OrderItem orderItem = new OrderItem();
	    User user = findUser.get();
	    Product product = findProduct.get();
	    Address address = addressExists.get();

	    order.setUser(user);
	    order.setOrderDate(LocalDateTime.now());
	    order.setShippingAddress(address.getFullAddress());
	    order.setOrderStatus(OrderStatus.PROCESSING);
	    order.setPaymentStatus(PaymentStatus.PENDING);
	    order.setTotalPrice(orderDetails.getQuantity() * product.getProductPrice());
	    
	    // Stock Checking
	    int newStock = product.getProductQuantity() - orderDetails.getQuantity();
	    if (newStock < 0) {
	        throw new CustomException("Out Of Stock.");
	    }
	    product.setProductQuantity(newStock);
	    productRepo.save(product);

	    // Update current user's cart
	    int remainingQty = cartItem.getProductQuantity() - orderDetails.getQuantity();
	    if (remainingQty <= 0) {
	        cartItemRepo.delete(cartItem);
	    } else {
	        cartItem.setProductQuantity(remainingQty);
	        cartItem.setTotalPrice(remainingQty * product.getProductPrice());
	        cartItemRepo.save(cartItem);
	    }

	    orderItem.setOrder(order);
	    orderItem.setProduct(product);
	    orderItem.setQuantity(orderDetails.getQuantity());
	    order.setItems(List.of(orderItem));
	    orderRepo.save(order);

	    ApiResponse<OrderProduct> response = new ApiResponse<>();
	    response.setData(order);
	    response.setMessage("Order Placed Successfully");
	    return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByUser(Long userId) {
		
		List<OrderProduct> orders = orderRepo.findByUser(userId);
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(!userRepo.findById(userId).isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		
		boolean isSelf = currUser.getUserId().equals(userId);
		boolean isManager = IsAuthorized.isManager(currUser.getUserPermissions());
		boolean isOrderManager = IsAuthorized.isOrderManager(currUser.getUserPermissions());
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		if(isSelf || (isManager || isOrderManager)) {
			response.setData(orders);
			response.setMessage("User "+userId+" Orders Details");
		}
		else if (!isSelf && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("Not Authorized to View This User's Order Details");
		}
		else if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("You don't have rights to view order details");
		}
		if(orders.isEmpty()) {
			throw new UserNotFoundException("No Order Details Found with Given User ID");
		}
		
		return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(Long userId, Long productId, int quantity) {

	    User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Authorized to Cancel Order With Another Account");
		}
		
	    Optional<OrderProduct> orderExists = orderRepo.findByUserAndProductAndQuantity(userId, productId, quantity);
	    if (!orderExists.isPresent()) {
	        throw new ProductNotFoundException("No matching order found for user " + userId + " with product " 
	        								+ productId + " and quantity " + quantity);
	    }
	    OrderProduct order = orderExists.get();

	    OrderItem items = null;
	    for (OrderItem item : order.getItems()) {
	        if (item.getProduct().getProductId().equals(productId) && item.getQuantity() == quantity) {
	            items = item;
	            break;
	        }
	    }

	    if (items == null) {
	        throw new ProductNotFoundException("No matching item found in order for product " + productId);
	    }
	    
	    Optional<Product> productExists = productRepo.findById(productId);

	    Product product = productExists.get();
	    product.setProductQuantity(product.getProductQuantity() + items.getQuantity());
	    productRepo.save(product);
	    orderRepo.delete(order);

	    ApiResponse<OrderProduct> response = new ApiResponse<>();
	    response.setMessage("Order cancelled successfully");
	    return ResponseEntity.ok(response);
	}


	public ResponseEntity<ApiResponse<List<OrderProduct>>> getByUserIdAndProductId(Long userId, Long productId) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		boolean isSelf = currUser.getUserId().equals(userId);
		boolean isManager = IsAuthorized.isManager(currUser.getUserPermissions());
		boolean isOrderManager = IsAuthorized.isOrderManager(currUser.getUserPermissions());
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		List<OrderProduct> orders = orderRepo.findAllByUserAndProduct(userId, productId);
		if(isSelf || (isManager || isOrderManager)) {
			response.setData(orders);
			response.setMessage("User "+userId+" Orders Details");
		}
		else if (!isSelf && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("Not Authorized to View This User's Order Details");
		}
		else if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("You don't have rights to view order details");
		}
		if(orders.isEmpty()) {
			throw new ProductNotFoundException("Orders Not Found With This UserID "+userId+" and ProductID "+productId);
		}
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders() {
		
		List<OrderProduct> orderList = orderRepo.findAll();
		
		if(orderList.isEmpty()) {
			throw new CustomException("No Order Found");
		}
		
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orderList);
		response.setMessage("All Orders Details");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderStatus(OrderStatus status) {
		
		List<OrderProduct> orders = orderRepo.findAllByOrderStatus(status);
		if(orders.isEmpty()) {
			throw new CustomException("No Order Found with Order Status "+status);
		}
		
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Order Status "+status);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByPayment(PaymentStatus paymentStatus) {
		
		List<OrderProduct> orders = orderRepo.findAllByPaymentStatus(paymentStatus);
		if(orders.isEmpty()) {
			throw new CustomException("No Orders Found With Payment Status "+paymentStatus);
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Payment Status "+paymentStatus);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<OrderProduct>> updateOrderStatus(Long orderId, OrderStatus status) {
		
		Optional<OrderProduct> orderExists = orderRepo.findById(orderId);
		
		orderExists.get().setOrderStatus(status);
		ApiResponse<OrderProduct> response = new ApiResponse<>();
		response.setData(orderExists.get());
		response.setMessage(" Order Status for"+" Order ID "+orderExists.get().getOrderId() +" Updated Sucessfully");
		return ResponseEntity.ok(response);
	}

}