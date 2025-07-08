package com.example.dto;

import java.util.List;

import com.example.model.Address;
import com.example.model.PaymentInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegisterUser {
	
	private String userName;
	private String userEmail;
	private String userPassword;
	
	@NotNull(message = " Shipping Address Cannot Be Null")
	private List<@Valid @NotNull Address> shippingAddress;
	@NotNull(message = " payment Details Cannot Be Null")
	private List<@Valid @NotNull PaymentInfo> paymentDetails;
	
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

}
