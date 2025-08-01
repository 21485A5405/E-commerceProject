package com.example.dto;

import java.util.List;

import com.example.model.Address;
import com.example.model.PaymentInfo;
import com.example.model.User;

public class UpdateUser {

	private String userName;
	private String userEmail;
	
	private List<Address> shippingAddress;
	private List<PaymentInfo> paymentDetails;
	public String getUserName() {
		return userName;
	}
	public UpdateUser(User user) {
		this.userName = user.getUserName();
		this.userEmail = user.getUserEmail();
		this.shippingAddress = user.getShippingAddress();
		this.paymentDetails = user.getPaymentDetails();
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
