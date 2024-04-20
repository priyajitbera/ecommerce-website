package com.priyajit.order.management.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class OrderNotFoundException extends ResponseStatusException {
    public OrderNotFoundException(String orderId) {
        super(HttpStatus.NOT_FOUND, String.format("No Order found with id:%s", orderId));
    }

    public OrderNotFoundException(String paymentId, String dummy) {
        super(HttpStatus.NOT_FOUND, String.format("No Order found with paymentId:%s", paymentId));
    }

    public static Supplier<OrderNotFoundException> supplier(String orderId) {
        return () -> new OrderNotFoundException(orderId);
    }

    public static Supplier<OrderNotFoundException> supplierByPaymentId(String paymentId) {
        return () -> new OrderNotFoundException(paymentId, null);
    }
}
