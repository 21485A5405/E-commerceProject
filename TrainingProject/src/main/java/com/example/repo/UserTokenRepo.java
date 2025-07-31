package com.example.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.User;
import com.example.model.UserToken;

import jakarta.transaction.Transactional;

public interface UserTokenRepo extends JpaRepository<UserToken, Long>{
	
	Optional<UserToken> findByUserToken(String token);
	
	UserToken findByUser(User user);
	
	@Query("SELECT u FROM UserToken u WHERE u.generatedAt <= :expiry")
	List<UserToken> findExpiredTokens(@Param("expiry") LocalDateTime expiry);

	@Modifying
    @Transactional
    @Query("DELETE FROM UserToken o WHERE o.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
	


}
