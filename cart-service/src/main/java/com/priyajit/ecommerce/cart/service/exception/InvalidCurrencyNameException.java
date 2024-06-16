package com.priyajit.ecommerce.cart.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.function.Supplier;

public class InvalidCurrencyNameException extends ResponseStatusException {

    public InvalidCurrencyNameException(String currencyName) {
        super(HttpStatus.BAD_REQUEST, String.format("Given currency name:%s is invalid", currencyName));
    }

    public InvalidCurrencyNameException(String currencyName, Collection<String> validCurrencies) {
        super(HttpStatus.BAD_REQUEST, String.format("Given currency name:%s is invalid, valid currencies are: %s",
                currencyName, validCurrencies));
    }

    public static Supplier<InvalidCurrencyNameException> supplier(String currencyName) {
        return () -> new InvalidCurrencyNameException(currencyName);
    }

    public static Supplier<InvalidCurrencyNameException> supplier(String currencyName, Collection<String> validCurrencies) {
        return () -> new InvalidCurrencyNameException(currencyName, validCurrencies);
    }
}
