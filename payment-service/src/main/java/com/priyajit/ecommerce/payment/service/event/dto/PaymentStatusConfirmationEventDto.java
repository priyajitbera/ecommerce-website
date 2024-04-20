package com.priyajit.ecommerce.payment.service.event.dto;

import com.priyajit.ecommerce.payment.service.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusConfirmationEventDto implements Serializable {

    private String paymentId;
    private PaymentStatus paymentStatus;
}
