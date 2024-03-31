package com.priyajit.ecommerce.user.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.function.Supplier;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }

    public UserNotFoundException(BigInteger userId) {
        super(HttpStatus.NOT_FOUND, String.format("User not found with id %s:", userId));
    }

    public static Supplier<UserNotFoundException> supplier(BigInteger userId) {
        return () -> new UserNotFoundException(userId);
    }
}
