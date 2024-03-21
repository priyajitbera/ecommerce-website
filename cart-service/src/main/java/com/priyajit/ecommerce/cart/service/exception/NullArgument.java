package com.priyajit.ecommerce.cart.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NullArgument extends ResponseStatusException {
    public NullArgument(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
