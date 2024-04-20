package com.priyajit.ecommerce.payment.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class PaymentNotFoundException extends ResponseStatusException {
    public PaymentNotFoundException(String paymentId) {
        super(HttpStatus.NOT_FOUND, String.format("No payment found with id: %s", paymentId));
    }

    public static Supplier<PaymentNotFoundException> supplier(String paymentId) {
        return () -> new PaymentNotFoundException(paymentId);
    }
}
