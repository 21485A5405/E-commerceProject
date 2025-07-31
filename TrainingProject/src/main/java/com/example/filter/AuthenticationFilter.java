package com.example.filter;
 
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
 
import com.example.authentication.CurrentUser;
import com.example.exception.UnAuthorizedException;
import com.example.model.UserToken;
import com.example.repo.UserTokenRepo;
 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
 
 
    private UserTokenRepo userTokenRepo;
    private CurrentUser currentUser;
    public AuthenticationFilter(UserTokenRepo userTokenRepo, CurrentUser currentUser) {
    	this.userTokenRepo = userTokenRepo;
    	this.currentUser = currentUser;
    }
 
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
 
    	try {
    		String path = request.getRequestURI();
 
          
    		if (path.equals("/users/register-user") || path.equals("/orders/get-payments")|| path.equals("/orders/get-orderstatus") || path.equals("/users/get-payment-details") || path.equals("/products/getall") || path.equals("/users/login-user") || path.equals("/admins/login-admin")) {
                filterChain.doFilter(request, response);
                return;
            }
	        String token = request.getHeader("Authorization");
	        System.out.println("Token received: " + token);
	        System.out.println();
	        if (token != null && !token.isBlank()) {
	            UserToken userToken =  userTokenRepo.findByUserToken(token)
	                .orElseThrow(() -> new UnAuthorizedException("Token Not Found / Token is Expired"));
	            currentUser.setUser(userToken.getUser());
	        }
	        
	        filterChain.doFilter(request, response);
    	}catch (UnAuthorizedException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.getWriter().write("{ \"Message\": \"" + ex.getMessage() + "\" }");
        }
    }
}
