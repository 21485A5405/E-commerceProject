package com.example.DTO;

import java.util.List;
import java.util.Set;

import com.example.model.Address;
import com.example.model.AdminPermissions;
import com.example.model.PaymentInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegisterAdmin {

    private String userName;
    private String userEmail;
    private String userPassword;

    @NotNull(message = "Shipping address is required")
    private List<@Valid @NotNull Address> shippingAddress;

    @NotNull(message = "Payment details are required")
    private List<@Valid @NotNull PaymentInfo> paymentDetails;

    @NotNull(message = "Permissions are required")
    private Set<AdminPermissions> userPermissions;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public List<Address> getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(List<Address> shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public List<PaymentInfo> getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(List<PaymentInfo> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public Set<AdminPermissions> getUserPermissions() {
		return userPermissions;
	}

	public void setUserPermissions(Set<AdminPermissions> userPermissions) {
		this.userPermissions = userPermissions;
	}
    
    
}
