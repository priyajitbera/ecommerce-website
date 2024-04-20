package com.priyajit.order.management.service.client.model;

import com.priyajit.order.management.service.domain.CardType;
import com.priyajit.order.management.service.domain.PaymentMode;
import com.priyajit.order.management.service.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentModel {

    private String id;
    private ZonedDateTime createdOn;
    private PaymentMode paymentMode;
    private BigDecimal amount;
    private String currency;
    private CardInfoModel cardInfo;
    private UpiInfoMode upiInfo;
    private PaymentStatus paymentStatus;
    private String failureReason;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardInfoModel {

        private String id;
        private CardType cardType;
        private String cardNumber;
        private String bankName;
        private String cardHolderName;
        private PaymentModel paymentModel;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpiInfoMode {
        private String id;
        private String upiId;
        private PaymentModel paymentModel;
    }
}
