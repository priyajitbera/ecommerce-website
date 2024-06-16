package com.priyajit.order.management.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomConstraintViolationException extends ResponseStatusException {
    public CustomConstraintViolationException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
