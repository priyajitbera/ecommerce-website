package com.priyajit.order.management.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NullArgumentException extends ResponseStatusException {
    public NullArgumentException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public NullArgumentException(String argName, String argType) {
        super(HttpStatus.BAD_REQUEST, String.format("Unexpected null value for arg %s:%s", argName, argType));
    }

    public NullArgumentException(String argName, Class argType) {
        super(HttpStatus.BAD_REQUEST, String.format("Unexpected null value for arg %s:%s", argName, argType.getSimpleName()));
    }
}
