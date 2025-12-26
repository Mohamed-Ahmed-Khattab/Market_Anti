package com.example.demo.exception;

public class UserEmailExsist extends RuntimeException {
    public UserEmailExsist(String message) {
        super(message);
    }
}
