package com.priyajit.ecommerce.payment.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidPaymentIStatusException extends ResponseStatusException {
    public InvalidPaymentIStatusException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
