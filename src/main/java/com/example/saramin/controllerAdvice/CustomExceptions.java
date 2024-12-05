package com.example.saramin.controllerAdvice;

public class CustomExceptions {
    public static class InvalidRegisterCredentialsException extends Exception {
        public InvalidRegisterCredentialsException(String message) {
            super(message);
        }
    }

    public static class InvalidTokenException extends Exception {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    public static class InvalidLoginCredentialsException extends Exception {
        public InvalidLoginCredentialsException(String message) { super(message); }
    }

    public static class InvalidProfileCredentialsException extends Exception {
        public InvalidProfileCredentialsException(String message) { super(message); }
    }
}