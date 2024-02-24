package com.priyajit.ecommerce.product.catalog.service.exception;

import java.util.function.Supplier;

public class ProductCategoryNotFoundException extends NotFoundException {

    public ProductCategoryNotFoundException(String productCategoryId) {
        super(String.format("No ProductCategory found with id:%s", productCategoryId));
    }

    public static Supplier<? extends RuntimeException> supplier(String productCategoryId) {
        return () -> new ProductCategoryNotFoundException(productCategoryId);
    }
}
