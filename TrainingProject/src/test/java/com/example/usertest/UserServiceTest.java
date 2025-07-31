package com.example.usertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.enums.Role;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.User;
import com.example.repo.AddressRepo;
import com.example.repo.CartItemRepo;
import com.example.repo.OrderRepo;
import com.example.repo.UserRepo;
import com.example.repo.UserTokenRepo;
import com.example.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@Mock private CartItemRepo cartItemRepo;
	@Mock private OrderRepo orderRepo;
	@Mock private UserRepo userRepo;
	@Mock private CurrentUser currentUser;
	@Mock private UserTokenRepo userTokenRepo;
	@Mock private AddressRepo addressRepo;
	
	@InjectMocks private UserServiceImpl userService;
	
	@Test
	void testSelfDeleteUserById() {
	    User current = new User();
	    current.setUserId(1L); // Logged-In User
	    current.setUserRole(Role.CUSTOMER);

	    User target = new User();
	    target.setUserId(1L);// Target User
	    target.setUserRole(Role.CUSTOMER);

	    when(currentUser.getUser()).thenReturn(current);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(target));

	    ResponseEntity<ApiResponse<User>> response = userService.deleteUserById(1L);

	    assertEquals("User Deleted Successfully", response.getBody().getMessage());
	    verify(cartItemRepo).deleteAllByUser(1L);
	    verify(orderRepo).deleteAllByUserId(1L);
	    verify(userTokenRepo).deleteByUserId(1L);
	    verify(addressRepo).deleteAllByUserId(1L);
	    verify(userRepo).deleteById(1L);
	}

	@Test
	void testUserNotFound() {
	    User current = new User(); // logged-In User
	    current.setUserId(1L);
	    current.setUserRole(Role.CUSTOMER);

	    when(currentUser.getUser()).thenReturn(current);
	    when(userRepo.findById(1L)).thenReturn(Optional.empty());

	    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L));
	    assertEquals("User Not Found", exception.getMessage());
	}

	@Test
	void testDeleteAnotherUser() {
	    User current = new User();
	    current.setUserId(2L); // Logged-In user
	    current.setUserRole(Role.CUSTOMER);

	    User target = new User();
	    target.setUserId(1L); // Target user

	    when(currentUser.getUser()).thenReturn(current);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(target));

	    UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> userService.deleteUserById(1L));
	    assertEquals("You Are Not Allowed To Delete Another User", exception.getMessage());
	}

	
}
