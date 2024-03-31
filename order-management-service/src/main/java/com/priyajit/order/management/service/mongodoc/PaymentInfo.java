package com.priyajit.order.management.service.mongodoc;

import com.priyajit.order.management.service.domain.PaymentMode;
import com.priyajit.order.management.service.domain.PaymentStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfo {

    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private CardInfo cardInfo;
    private UpiInfo upiInfo;
}
