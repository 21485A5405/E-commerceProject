package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.advicemethods.IsAuthorized;
import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.dto.GetOrders;
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
import com.example.repo.OrderItemRepo;
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
	private OrderItemRepo orderItemRepo;
	
	public OrderServiceImpl(UserRepo userRepo, OrderItemRepo orderItemRepo, CurrentUser currentUser, CartItemRepo cartItemRepo, ProductRepo productRepo, OrderRepo orderRepo, AddressRepo addressRepo) {
		this.userRepo = userRepo;
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
		this.addressRepo = addressRepo;
		this.currentUser = currentUser;
		this.orderItemRepo = orderItemRepo;
	}

	@Transactional
	public ResponseEntity<ApiResponse<List<GetOrders>>> placeOrder(List<PlaceOrder> orderDetailsList) {

	    User currUser = currentUser.getUser();
	    if (currUser == null) {
	        throw new UnAuthorizedException("Please Login");
	    }

	    List<GetOrders> placedOrderDTOs = new ArrayList<>();

	    for (PlaceOrder orderDetails : orderDetailsList) {

	        Optional<User> findUser = userRepo.findById(orderDetails.getUserId());
	        Optional<Product> findProduct = productRepo.findById(orderDetails.getProductId());
	        Optional<CartItem> cart = cartItemRepo.findByUserAndProduct(orderDetails.getUserId(), orderDetails.getProductId());
	        Optional<Address> addressExists = addressRepo.findById(orderDetails.getAddressId());

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

	        if (!addressExists.get().getUser().getUserId().equals(orderDetails.getUserId())) {
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
	        order.setOrderStatus(OrderStatus.PENDING);
	        order.setPaymentStatus(PaymentStatus.PENDING);
	        order.setTotalPrice(orderDetails.getQuantity() * product.getProductPrice());

	        // Stock Checking
	        int newStock = product.getProductQuantity() - orderDetails.getQuantity();
	        if (newStock < 0) {
	            throw new CustomException("Out Of Stock.");
	        }
	        product.setProductQuantity(newStock);
	        productRepo.save(product);

	        // Update cart
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

	        // Map to DTO
	        placedOrderDTOs.add(new GetOrders(order, orderItem));
	    }

	    ApiResponse<List<GetOrders>> response = new ApiResponse<>();
	    response.setData(placedOrderDTOs);
	    response.setMessage("Orders Placed Successfully");

	    return ResponseEntity.ok(response);
	}

	public ResponseEntity<List<GetOrders>> getOrderByUser(Long userId) {
	    User currUser = currentUser.getUser();
	    if (currUser == null) {
	        throw new UnAuthorizedException("Please Login");
	    }

	    if (!userRepo.findById(userId).isPresent()) {
	        throw new UserNotFoundException("User Not Found");
	    }

	    boolean isSelf = currUser.getUserId().equals(userId);
	    boolean isManager = IsAuthorized.isManager(currUser.getUserPermissions());
	    boolean isOrderManager = IsAuthorized.isOrderManager(currUser.getUserPermissions());

	    if (!isSelf && !(isManager || isOrderManager)) {
	        throw new UnAuthorizedException("Not Authorized to View This User's Order Details");
	    }

	    if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
	        throw new UnAuthorizedException("You don't have rights to view order details");
	    }

	    List<OrderProduct> orders = orderRepo.findByUser(userId);
	    if (orders.isEmpty()) {
	        throw new UserNotFoundException("No Order Details Found with Given User ID");
	    }

	    List<GetOrders> dtoList = new ArrayList<>();
	    for (OrderProduct order : orders) {
	        for (OrderItem item : order.getItems()) {
	            dtoList.add(new GetOrders(order, item));
	        }
	    }

	    return ResponseEntity.ok(dtoList);
	}


	@Transactional
	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(Long orderId) {

		Optional<OrderProduct> exists = orderRepo.findById(orderId);
		Optional<OrderItem> items  = orderItemRepo.findByOrder_OrderId(orderId);
	    User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= currUser.getUserId()) {
			throw new UnAuthorizedException("Not Authorized to Cancel Order With Another Account");
		}

	    OrderProduct order = exists.get();

	    Optional<Product> productExists = productRepo.findById(items.get().getProduct().getProductId());

	    Product product = productExists.get();
	    product.setProductQuantity(product.getProductQuantity() + items.get().getQuantity());
	    productRepo.save(product);
	    order.setOrderStatus(OrderStatus.CANCELLED);
	    order.setPaymentStatus(PaymentStatus.REFUND_INITIATED);
	    orderRepo.save(order);

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

	public ResponseEntity<ApiResponse<List<GetOrders>>> getAllOrders() {
	    List<OrderProduct> orderList = orderRepo.findAll();

	    if (orderList.isEmpty()) {
	        throw new CustomException("No Order Found");
	    }

	    List<GetOrders> dtoList = new ArrayList<>();

	    for (OrderProduct order : orderList) {
	        for (OrderItem item : order.getItems()) {
	            dtoList.add(new GetOrders(order, item));
	        }
	    }

	    ApiResponse<List<GetOrders>> response = new ApiResponse<>();
	    response.setData(dtoList);
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
		orderRepo.save(orderExists.get());
		ApiResponse<OrderProduct> response = new ApiResponse<>();
		response.setData(orderExists.get());
		response.setMessage(" Order Status for"+" Order ID "+orderExists.get().getOrderId() +" Updated Sucessfully");
		return ResponseEntity.ok(response);
	}


	public ResponseEntity<List<GetOrders>> getByOrderId(Long orderId) {
		User currUser = currentUser.getUser();
	    if (currUser == null) {
	        throw new UnAuthorizedException("Please Login");
	    }

	    Optional<OrderProduct> optionalOrder = orderRepo.findById(orderId);
	    if (!optionalOrder.isPresent()) {
	        throw new UserNotFoundException("Order Not Found with Given ID");
	    }

	    OrderProduct order = optionalOrder.get();
	    Long orderUserId = order.getUser().getUserId();

	    boolean isSelf = currUser.getUserId().equals(orderUserId);
	    boolean isManager = IsAuthorized.isManager(currUser.getUserPermissions());
	    boolean isOrderManager = IsAuthorized.isOrderManager(currUser.getUserPermissions());

	    if (!isSelf && !(isManager || isOrderManager)) {
	        throw new UnAuthorizedException("Not Authorized to View This Order");
	    }

	    if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
	        throw new UnAuthorizedException("You don't have rights to view order details");
	    }

	    List<GetOrders> dtoList = new ArrayList<>();
	    for (OrderItem item : order.getItems()) {
	        dtoList.add(new GetOrders(order, item));
	    }

	    return ResponseEntity.ok(dtoList);
	}

	public ResponseEntity<ApiResponse<OrderProduct>> updatePaymentStatus(Long orderId, PaymentStatus status) {
		
		Optional<OrderProduct> orderExists = orderRepo.findById(orderId);
		
		orderExists.get().setPaymentStatus(status);
		orderRepo.save(orderExists.get());
		ApiResponse<OrderProduct> response = new ApiResponse<>();
		response.setData(orderExists.get());
		response.setMessage(" Payment Status for"+" Order ID "+orderExists.get().getOrderId() +" Updated Sucessfully");
		return ResponseEntity.ok(response);
	}

}