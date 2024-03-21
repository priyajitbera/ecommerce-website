package com.priyajit.ecommerce.cart.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class BadProductQuantityValueException extends ResponseStatusException {
    public BadProductQuantityValueException(String productId) {
        super(HttpStatus.BAD_REQUEST, String.format("No Product found with id:%s", productId));
    }

    public static Supplier<BadProductQuantityValueException> supplier(String productId) {
        return () -> new BadProductQuantityValueException(productId);
    }
}
