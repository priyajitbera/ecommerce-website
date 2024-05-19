package com.priyajit.ecommerce.payment.service.controller;

import com.priyajit.ecommerce.payment.service.dto.ConfirmPaymentStatusDto;
import com.priyajit.ecommerce.payment.service.dto.CreatePaymentDto;
import com.priyajit.ecommerce.payment.service.model.PaymentModel;
import com.priyajit.ecommerce.payment.service.model.Response;
import com.priyajit.ecommerce.payment.service.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.priyajit.ecommerce.payment.service.controller.ControllerHelper.supplyResponse;

@Slf4j
@RestController
@RequestMapping("/v1/payment")
public class PaymentControllerV1 {

    private PaymentService paymentService;

    public PaymentControllerV1(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Response<PaymentModel>> createPayment(
            @RequestBody CreatePaymentDto dto
    ) {
        return supplyResponse(() -> paymentService.createPayment(dto), log);
    }

    @GetMapping
    public ResponseEntity<Response<PaymentModel>> findPayment(
            @RequestParam(name = "paymentId") String paymentId
    ) {
        return supplyResponse(() -> paymentService.findPayment(paymentId), log);
    }

    @PostMapping("/confirm-payment-status")
    public ResponseEntity<Response<PaymentModel>> confirmPaymentStatus(
            @RequestBody ConfirmPaymentStatusDto dto
    ) {
        return supplyResponse(() -> paymentService.confirmPaymentStatus(dto), log);
    }
}
