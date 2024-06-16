package com.priyajit.ecommerce.user.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistsException extends ResponseStatusException {

    public UserAlreadyExistsException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public UserAlreadyExistsException(String emailId, int dummy) {
        super(HttpStatus.BAD_REQUEST, String.format("User already exists with emailId: %s:", emailId));
    }
}
