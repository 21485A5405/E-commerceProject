package com.example.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.enums.Role;
import com.example.model.PaymentInfo;
import com.example.model.PaymentMethod;
import com.example.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	
	Optional<User> findByUserEmail(String eMail);
	
	@Query("SELECT u.userId FROM User u")
    List<Long> getAllUserIds();
	
	Optional<User> findByUserRole(Role role);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userPermissions WHERE u.id = :id")
	Optional<User> findByIdWithPermissions(@Param("id") Long id);
	
	@Query("SELECT u.paymentDetails FROM User u WHERE u.userId = :userId")
	List<PaymentInfo> findPaymentDetailsByUserId(@Param("userId") Long userId);

}
