package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
    	
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    } 

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
    	
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomeException(CustomException ex) {
    	
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<String> handleAdminNotFoundException(AdminNotFoundException ex) {
    	
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<String> handleUnAuthorized(UnAuthorizedException ex) {
    	
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");

        return ResponseEntity.badRequest().body(message);
    }
}

