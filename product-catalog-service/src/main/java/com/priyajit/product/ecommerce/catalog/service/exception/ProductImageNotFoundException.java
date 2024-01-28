package com.priyajit.product.ecommerce.catalog.service.exception;

import java.util.function.Supplier;

public class ProductImageNotFoundException extends NotFoundException {

    public ProductImageNotFoundException(String productImageId) {
        super(String.format("No ProductImage found with id:%s", productImageId));
    }

    public static Supplier<? extends RuntimeException> supplier(String productCategoryId) {
        return () -> new ProductImageNotFoundException(productCategoryId);
    }
}
