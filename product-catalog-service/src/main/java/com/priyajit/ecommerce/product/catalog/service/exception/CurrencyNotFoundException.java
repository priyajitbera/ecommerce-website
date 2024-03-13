package com.priyajit.ecommerce.product.catalog.service.exception;

import java.util.function.Supplier;

public class CurrencyNotFoundException extends NotFoundException {

    public CurrencyNotFoundException(String currencyId) {
        super(String.format("No Currency found with id:%s", currencyId));
    }

    public static Supplier<? extends RuntimeException> supplier(String currencyId) {
        return () -> new CurrencyNotFoundException(currencyId);
    }
}
