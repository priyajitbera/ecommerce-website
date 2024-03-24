package com.priyajit.ecommerce.cart.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class ExchangeRateNotAvailableException extends ResponseStatusException {
    public ExchangeRateNotAvailableException(String baseCurrency, String toCurrency) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                String.format("Exchange rate not available for baseCurrency:%s, toCurrency:%s", baseCurrency, toCurrency)
        );
    }

    public static Supplier<ExchangeRateNotAvailableException> supplier(String baseCurrency, String toCurrency) {
        return () -> new ExchangeRateNotAvailableException(baseCurrency, toCurrency);
    }
}
