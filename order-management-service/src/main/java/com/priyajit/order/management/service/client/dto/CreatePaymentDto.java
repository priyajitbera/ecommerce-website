package com.priyajit.order.management.service.client.dto;

import com.priyajit.order.management.service.domain.CardType;
import com.priyajit.order.management.service.domain.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardInfoDto {
        private CardType cardType;
        private String cardNumber;
        private String bankName;
        private Integer validUptoMonth;
        private Integer validUptoYear;
        private String cvv;
        private String cardHolderName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpiInfoDto {
        private String upiId;
    }

    private String orderId;
    private PaymentMode paymentMode;
    private CardInfoDto cardInfo;
    private UpiInfoDto upiInfo;
    private BigDecimal amount;
    private String currency;
}
