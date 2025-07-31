package com.example.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.model.OrderProduct;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepo extends JpaRepository<OrderProduct, Long> {


    @Query("SELECT o FROM OrderProduct o JOIN o.orderItems oi WHERE oi.product.productId = :productId")
    List<OrderProduct> findOrdersByProduct(@Param("productId") Long productId);

  
    @Query("SELECT o FROM OrderProduct o JOIN o.orderItems oi WHERE o.user.userId = :userId AND oi.product.productId = :productId")
    List<OrderProduct> findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    
    @Query("SELECT o FROM OrderProduct o WHERE o.user.userId = :userId")
    List<OrderProduct> findByUser(@Param("userId") Long userId);

    @Query("SELECT o FROM OrderProduct o JOIN o.orderItems oi WHERE o.user.userId = :userId AND oi.product.productId = :productId")
    List<OrderProduct> findAllByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderProduct o WHERE o.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(" DELETE FROM OrderProduct o WHERE o.user.userId = :userId AND o IN (SELECT oi.order FROM OrderItem oi WHERE oi.product.productId = :productId AND oi.quantity = :quantity)")
    void deleteByQuantity(@Param("userId") Long userId, 
                          @Param("productId") Long productId, 
                          @Param("quantity") int quantity);

    @Query("SELECT o FROM OrderProduct o WHERE o.orderStatus = :status")
    List<OrderProduct> findAllByOrderStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM OrderProduct o WHERE o.paymentStatus = :paymentStatus")
    List<OrderProduct> findAllByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);

    @Query("SELECT DISTINCT o FROM OrderProduct o JOIN o.orderItems oi WHERE o.user.userId = :userId AND oi.product.productId = :productId AND oi.quantity = :quantity")
    Optional<OrderProduct> findByUserAndProductAndQuantity(@Param("userId") Long userId,
                                                           @Param("productId") Long productId,
                                                           @Param("quantity") int quantity);
}
