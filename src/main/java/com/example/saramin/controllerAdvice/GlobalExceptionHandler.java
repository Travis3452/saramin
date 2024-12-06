package com.example.saramin.controllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomExceptions.InvalidRegisterCredentialsException.class)
    public ResponseEntity<String> handleInvalidRegisterCredentialsException(CustomExceptions.InvalidRegisterCredentialsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(CustomExceptions.InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(CustomExceptions.InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(CustomExceptions.InvalidLoginCredentialsException.class)
    public ResponseEntity<String> handleInvalidLoginCredentialsException(CustomExceptions.InvalidLoginCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(CustomExceptions.InvalidProfileCredentialsException.class)
    public ResponseEntity<String> handleInvalidProfileCredentialsException(CustomExceptions.InvalidProfileCredentialsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
