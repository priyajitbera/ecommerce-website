package com.priyajit.product.ecommerce.catalog.service.exception;

import java.util.function.Supplier;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(String productId) {
        super(String.format("No Product found with id:%s", productId));
    }

    public static Supplier<? extends RuntimeException> supplier(String productId) {
        return () -> new ProductNotFoundException(productId);
    }
}
