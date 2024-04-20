package com.priyajit.ecommerce.payment.service.controller;

import com.priyajit.ecommerce.payment.service.dto.ConfirmPaymentStatusDto;
import com.priyajit.ecommerce.payment.service.dto.CreatePaymentDto;
import com.priyajit.ecommerce.payment.service.model.PaymentModel;
import com.priyajit.ecommerce.payment.service.model.Response;
import com.priyajit.ecommerce.payment.service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        try {
            var paymentModel = paymentService.createPayment(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<PaymentModel>builder()
                            .data(paymentModel)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<PaymentModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<PaymentModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<Response<PaymentModel>> findPayment(
            @RequestParam(name = "paymentId") String paymentId
    ) {
        try {
            var paymentModel = paymentService.findPayment(paymentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<PaymentModel>builder()
                            .data(paymentModel)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<PaymentModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<PaymentModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/confirm-payment-status")
    public ResponseEntity<Response<PaymentModel>> confirmPaymentStatus(
            @RequestBody ConfirmPaymentStatusDto dto
    ) {
        try {
            var paymentModel = paymentService.confirmPaymentStatus(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<PaymentModel>builder()
                            .data(paymentModel)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<PaymentModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<PaymentModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }
}
