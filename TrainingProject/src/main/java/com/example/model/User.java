	package com.example.model;
	
	import java.util.List;
	import java.util.Set;

import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
	
	import jakarta.persistence.CascadeType;
	import jakarta.persistence.CollectionTable;
	import jakarta.persistence.ElementCollection;
	import jakarta.persistence.Entity;
	import jakarta.persistence.EnumType;
	import jakarta.persistence.Enumerated;
	import jakarta.persistence.FetchType;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.JoinColumn;
	import jakarta.persistence.OneToMany;
	import lombok.NoArgsConstructor;
	
	@Entity
	@NoArgsConstructor
	public class User {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long userId;
		private String userName;
		private String userEmail;
		
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private String userPassword;
	
		@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
		private List<Address> shippingAddress;
	
	    @ElementCollection(fetch = FetchType.EAGER)
	    @CollectionTable(name = "paymentDetails", joinColumns = @JoinColumn(name = "userId"))
	    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	    private List<PaymentInfo> paymentDetails;
	
	    @Enumerated(EnumType.STRING)
	    private Role userRole;
	    
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<UserToken> tokens;

		
		@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
		@JsonIgnore
		private List<CartItem> cartItems;
		
		@ElementCollection(fetch = FetchType.EAGER)
		@CollectionTable(name = "userPermissions", joinColumns = @JoinColumn(name = "userId"))
		@Enumerated(EnumType.STRING)
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private Set<AdminPermissions> userPermissions;
		
		@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
		private List<OrderProduct> orders;
	
		
		public Set<AdminPermissions> getUserPermissions() {
			return userPermissions;
		}
	
		public void setUserPermissions(Set<AdminPermissions> userPermissions) {
			this.userPermissions = userPermissions;
		}
	
		public List<CartItem> getCartItems() {
			return cartItems;
		}
	
		public void setCartItems(List<CartItem> cartItems) {
			this.cartItems = cartItems;
		}
	
		public User() {
			
		}
	
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
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
		
		public Role getUserRole() {
			return userRole;
		}
	
		public void setUserRole(Role userRole) {
			this.userRole = userRole;
		}
		
		public List<PaymentInfo> getPaymentDetails() {
			return paymentDetails;
		}
		public void setPaymentDetails(List<PaymentInfo> paymentDetails) {
			this.paymentDetails = paymentDetails;
		}
		public String getUserPassword() {
			return userPassword;
		}
		public void setUserPassword(String userPassword) {
		    this.userPassword =userPassword;
		}
		public List<Address> getShippingAddress() {
			return shippingAddress;
		}
		public void setShippingAddress(List<Address> shippingAddress) {
			this.shippingAddress = shippingAddress;
		}
		
		public String displayPayments() {
		    StringBuilder builder = new StringBuilder();
		    if (paymentDetails != null) {
		        for (PaymentInfo info : paymentDetails) {
		            builder.append(info.getPaymentMethod()).append(" - ")
		                   .append(info.getAccountDetails()).append(", ");   
		        }
		    }
		    return builder.toString();
		}
	
	}
