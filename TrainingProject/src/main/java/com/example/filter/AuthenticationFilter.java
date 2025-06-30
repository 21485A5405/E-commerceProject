package com.example.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authentication.CurrentUser;
import com.example.exception.UnAuthorizedException;
import com.example.model.User;
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

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        System.out.println("Token received: " + token);


        if (token != null && !token.isBlank()) {
            UserToken userToken =  userTokenRepo.findByUserToken(token)
                .orElseThrow(() -> new UnAuthorizedException("Invalid or expired token"));

            currentUser.setUser(userToken.getUser());

        }

        filterChain.doFilter(request, response);
    }
}

