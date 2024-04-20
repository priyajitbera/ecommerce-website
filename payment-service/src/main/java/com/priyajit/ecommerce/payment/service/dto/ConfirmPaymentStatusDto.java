package com.priyajit.ecommerce.payment.service.dto;

import com.priyajit.ecommerce.payment.service.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentStatusDto {
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String failureReason;
}
