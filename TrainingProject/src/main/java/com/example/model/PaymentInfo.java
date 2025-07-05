package com.example.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

@ToString
@Embeddable
public class PaymentInfo {
	
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment Method cannot be null")
    private PaymentMethod paymentMethod;
    
    @NotNull(message = "Account Details cannot be null")
    private String accountDetails;

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getAccountDetails() {
		return accountDetails;
	}

	public void setAccountDetails(String accountDetails) {
		this.accountDetails = accountDetails;
	}
}
