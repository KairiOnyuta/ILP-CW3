package com.ilp.restapi.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions thrown by the Jakarta Bean Validation API 
     * when an @Valid or @Validated DTO fails validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        // Map to hold error messages for each invalid field
        Map<String, String> errors = new HashMap<>();
        
        // Extracts all validation errors
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // Gets the field name (e.g., 'lat' or 'position1.lat') and the error message
            errors.put(error.getField(), error.getDefaultMessage());
        });

        // Returns a 400 Bad Request status code with the detailed error messages
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
