package com.priyajit.order.management.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class UserNotFoundException extends ResponseStatusException {

    public UserNotFoundException(String userId) {
        super(HttpStatus.NOT_FOUND, String.format("User not found with id %s:", userId));
    }

    public static Supplier<UserNotFoundException> supplier(String userId) {
        return () -> new UserNotFoundException(userId);
    }
}
