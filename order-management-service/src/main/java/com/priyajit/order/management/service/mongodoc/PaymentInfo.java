package com.priyajit.order.management.service.mongodoc;

import com.priyajit.order.management.service.domain.PaymentMode;
import com.priyajit.order.management.service.domain.PaymentStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfo {

    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentStatusLastUpdatedOn;
    private String paymentId;
}
