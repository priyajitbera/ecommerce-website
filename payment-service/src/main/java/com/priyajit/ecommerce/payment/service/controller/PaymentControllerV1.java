package com.priyajit.ecommerce.payment.service.controller;

import com.priyajit.ecommerce.payment.service.dto.ConfirmPaymentStatusDto;
import com.priyajit.ecommerce.payment.service.dto.CreatePaymentDto;
import com.priyajit.ecommerce.payment.service.model.PaymentModel;
import com.priyajit.ecommerce.payment.service.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/payment")
public class PaymentControllerV1 {

    private PaymentService paymentService;

    public PaymentControllerV1(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentModel> createPayment(
            @RequestBody CreatePaymentDto dto
    ) {
        return ok(paymentService.createPayment(dto));
    }

    @GetMapping
    public ResponseEntity<PaymentModel> findPayment(
            @RequestParam(name = "paymentId") String paymentId
    ) {
        return ok(paymentService.findPayment(paymentId));
    }

    @PostMapping("/confirm-payment-status")
    public ResponseEntity<PaymentModel> confirmPaymentStatus(
            @RequestBody ConfirmPaymentStatusDto dto
    ) {
        return ok(paymentService.confirmPaymentStatus(dto));
    }
}
