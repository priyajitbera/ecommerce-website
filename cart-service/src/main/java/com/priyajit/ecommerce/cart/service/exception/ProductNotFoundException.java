package com.priyajit.ecommerce.cart.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class ProductNotFoundException extends ResponseStatusException {
    public ProductNotFoundException(String productId) {
        super(HttpStatus.BAD_REQUEST, String.format("No Product found with id:%s", productId));
    }

    public static Supplier<ProductNotFoundException> supplier(String productId) {
        return () -> new ProductNotFoundException(productId);
    }
}
