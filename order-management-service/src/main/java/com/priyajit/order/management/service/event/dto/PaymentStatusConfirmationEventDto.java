package com.priyajit.order.management.service.event.dto;

import com.priyajit.order.management.service.domain.PaymentStatus;
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
    private String failureReason;
}
