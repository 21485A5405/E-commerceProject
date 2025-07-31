package com.example.advicemethods;

import java.util.Set;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.example.authentication.CurrentUser;
import com.example.customannotations.ForOrders;
import com.example.customannotations.ForProduct;
import com.example.customannotations.ForUser;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.exception.UnAuthorizedException;
import com.example.model.User;

@Aspect
@Component
public class ValidUser {
	
	private CurrentUser currUser;
	
	public ValidUser(CurrentUser currUser) {

		this.currUser = currUser;
	}

	@Before("@annotation(forProduct)")
    public void validateForProducts(ForProduct forProduct) {
   
		User user = currUser.getUser();

        if (user == null) {
            throw new UnAuthorizedException("Please Login");
        }
        Role userRole = user.getUserRole();
        if(userRole == Role.CUSTOMER) {
        	throw new UnAuthorizedException("Access Denied For User");
        }
        AdminPermissions[] requiredPermissions = forProduct.validPermissions();
        Set<AdminPermissions> userPermissions = user.getUserPermissions();
        boolean isAuth = false;
        
        for(AdminPermissions permission : requiredPermissions) {
        	if(userPermissions.contains(permission)) {
        		isAuth = true;
        		break;
        	}
        }
       if(!isAuth) {
    	   throw new UnAuthorizedException("Only Manager and Product Manager Have Rights");
       }   
    }
	
	@Before("@annotation(forUsers)")
	public void validateForUsers(ForUser forUsers) {
		
		User user = currUser.getUser();

        if (user == null) {
            throw new UnAuthorizedException("Please Login");
        }
        Role userRole = user.getUserRole();
        if(userRole == Role.CUSTOMER) {
        	throw new UnAuthorizedException("Access Denied For User");
        }
        AdminPermissions[] requiredPermissions = forUsers.validPermissions();
        Set<AdminPermissions> userPermissions = user.getUserPermissions();
        boolean isAuth = false;
        
        for(AdminPermissions permission : requiredPermissions) {
        	if(userPermissions.contains(permission)) {
        		isAuth = true;
        		break;
        	}
        }
       if(!isAuth) {
    	   throw new UnAuthorizedException("Only Manager and User Manager Have Rights");
       }   
	}
	
	@Before("@annotation(forOrders)")
	public void validateForOrders(ForOrders forOrders) {
		
		User user = currUser.getUser();

        if (user == null) {
            throw new UnAuthorizedException("Please Login");
        }
        Role userRole = user.getUserRole();
        if(userRole == Role.CUSTOMER) {
        	throw new UnAuthorizedException("Access Denied For User");
        }
        AdminPermissions[] requiredPermissions = forOrders.validPermissions();
        Set<AdminPermissions> userPermissions = user.getUserPermissions();
        boolean isAuth = false;
        
        for(AdminPermissions permission : requiredPermissions) {
        	if(userPermissions.contains(permission)) {
        		isAuth = true;
        		break;
        	}
        }
       if(!isAuth) {
    	   throw new UnAuthorizedException("Only Manager and Order Manager Have Rights");
       }   
	}
}
