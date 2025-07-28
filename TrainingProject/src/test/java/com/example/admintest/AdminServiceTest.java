package com.example.admintest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.dto.RegisterAdmin;
import com.example.dto.UpdateUser;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.exception.CustomException;
import com.example.exception.UnAuthorizedException;
import com.example.model.Address;
import com.example.model.OrderProduct;
import com.example.model.PaymentInfo;
import com.example.model.PaymentMethod;
import com.example.model.User;
import com.example.repo.OrderRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;
import com.example.repo.UserTokenRepo;
import com.example.service.AdminServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
	
	@Mock private UserRepo userRepo;
	@Mock private ProductRepo productRepo;
	@Mock private OrderRepo orderRepo;
	@Mock private CurrentUser currentUser;
	@Mock private UserTokenRepo userTokenRepo;
	
	@InjectMocks AdminServiceImpl adminService;
	
	@Test
	void testCreateAdmin() {
	    RegisterAdmin dto = new RegisterAdmin();
	    dto.setUserEmail("new@admin.com");
	    dto.setUserName("New Admin");
	    dto.setUserPassword("password");
	    PaymentInfo paymentInfo = new PaymentInfo();
	    paymentInfo.setPaymentMethod(PaymentMethod.CREDIT_CARD);
	    paymentInfo.setAccountDetails("XXXX-XXXX-XXXX-1234");
	    List<PaymentInfo> list = new ArrayList<>();
	    list.add(paymentInfo);
	    dto.setUserPermissions(Set.of(AdminPermissions.User_Manager));

	    when(userRepo.findByUserEmail("new@admin.com")).thenReturn(Optional.empty());

	    ResponseEntity<ApiResponse<User>> response = adminService.createAdmin(dto);

	    assertEquals("New Admin Added Successfully", response.getBody().getMessage());
	    verify(userRepo).save(any(User.class));
	}

	@Test
	void testGetAdminById() {
	    User current = new User();
	    current.setUserId(1L);
	    current.setUserRole(Role.ADMIN);
	    current.setUserPermissions(Set.of(AdminPermissions.User_Manager));

	    User found = new User();
	    found.setUserId(1L);
	    found.setUserRole(Role.ADMIN);

	    when(currentUser.getUser()).thenReturn(current);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(found));

	    ResponseEntity<ApiResponse<User>> response = adminService.getAdminById(1L);

	    assertEquals("Admin Details", response.getBody().getMessage());
	    assertEquals(1L, response.getBody().getData().getUserId());
	}

//	@Test
//	void testUpdateAdminById() {
//	    UpdateUser dto = new UpdateUser();
//	    dto.setUserName("Updated Name");
//	    dto.setUserEmail("updated@admin.com");
//	    PaymentInfo paymentInfo = new PaymentInfo();
//	    paymentInfo.setPaymentMethod(PaymentMethod.DEBIT_CARD);
//	    paymentInfo.setAccountDetails("XXXX-XXXX-XXXX-5678");
//	    List<PaymentInfo> list = new ArrayList<>();
//	    list.add(paymentInfo);
//	    dto.setPaymentDetails(list);
//	    dto.setShippingAddress(List.of(new Address()));
//
//	    User current = new User();
//	    current.setUserId(1L);
//	    current.setUserRole(Role.ADMIN);
//
//	    User admin = new User();
//	    admin.setUserId(1L);
//	    admin.setShippingAddress(new ArrayList<>());
//
//	    when(currentUser.getUser()).thenReturn(current);
//	    when(userRepo.findById(1L)).thenReturn(Optional.of(admin));
//
//	    ResponseEntity<ApiResponse<User>> response = adminService.updateAdminById(1L, dto);
//
//	    assertEquals("Admin Updated Successfully", response.getBody().getMessage());
//	    verify(userRepo).save(admin);
//	}
	
	@Test
	void testDeleteAdminById() {
	    User current = new User();
	    current.setUserId(1L);
	    current.setUserRole(Role.ADMIN);

	    User admin = new User();
	    admin.setUserId(1L);
	    admin.setUserRole(Role.ADMIN);

	    when(currentUser.getUser()).thenReturn(current);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(admin));

	    ResponseEntity<ApiResponse<User>> response = adminService.deleteAdminById(1L);

	    assertEquals("Admin Deleted Successfully", response.getBody().getMessage());
	    verify(userTokenRepo).deleteByUserId(1L);
	    verify(userRepo).deleteById(1L);
	}

	@Test
	void testGetAllAdmins() {
	    User current = new User();
	    current.setUserId(1L);
	    current.setUserRole(Role.ADMIN);
	    current.setUserPermissions(Set.of(AdminPermissions.Manager));

	    User admin = new User();
	    admin.setUserRole(Role.ADMIN);

	    User user = new User();
	    user.setUserRole(Role.CUSTOMER);

	    when(currentUser.getUser()).thenReturn(current);
	    when(userRepo.findAll()).thenReturn(List.of(admin, user));

	    ResponseEntity<ApiResponse<List<User>>> response = adminService.getAllAdmins();

	    assertEquals("List Of Admins", response.getBody().getMessage());
	    assertEquals(1, response.getBody().getData().size());
	}

	@Test
	void testGetAllOrders() {
	    User current = new User();
	    current.setUserId(1L);
	    current.setUserRole(Role.ADMIN);
	    current.setUserPermissions(Set.of(AdminPermissions.Order_Manager, AdminPermissions.Manager));

	    OrderProduct order = new OrderProduct();

	    when(currentUser.getUser()).thenReturn(current);
	    when(orderRepo.findAll()).thenReturn(List.of(order));

	    ResponseEntity<ApiResponse<List<OrderProduct>>> response = adminService.getAllOrders();

	    assertEquals("All Orders Details", response.getBody().getMessage());
	    assertEquals(1, response.getBody().getData().size());
	}

	@Test
	void testWhenAdminAlreadyExists() {
	    RegisterAdmin dto = new RegisterAdmin();
	    dto.setUserEmail("admin@cognizant.com");

	    when(userRepo.findByUserEmail("admin@cognizant.com")).thenReturn(Optional.of(new User()));

	    CustomException exception = assertThrows(CustomException.class, () -> {
	        adminService.createAdmin(dto);
	    });

	    assertEquals("Admin Already Exists Please Login", exception.getMessage());
	}
	
	@Test
	void testWhenOnlyAdmins() {
		User currUser = new User();
		currUser.setUserId(1L);
		currUser.setUserRole(Role.CUSTOMER);
		
		User target = new User();
		target.setUserId(2L);
		target.setUserRole(Role.ADMIN);
		when(currentUser.getUser()).thenReturn(currUser);
		when(userRepo.findById(2L)).thenReturn(Optional.of(target));
		
		UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> adminService.getAdminById(2L));
		
		assertEquals("Users Dont Have Access", exception.getMessage());
		
	}

}
