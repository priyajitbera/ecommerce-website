package com.priyajit.ecommerce.payment.service.model;

import com.priyajit.ecommerce.payment.service.domain.CardType;
import com.priyajit.ecommerce.payment.service.domain.PaymentMode;
import com.priyajit.ecommerce.payment.service.domain.PaymentStatus;
import com.priyajit.ecommerce.payment.service.entity.CardInfo;
import com.priyajit.ecommerce.payment.service.entity.Payment;
import com.priyajit.ecommerce.payment.service.entity.UpiInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentModel {

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

        public static CardInfoModel buildFrom(@Nullable CardInfo cardInfo) {
            if (cardInfo == null) return null;

            return CardInfoModel.builder()
                    .id(cardInfo.getId())
                    .cardType(cardInfo.getCardType())
                    .cardNumber(cardInfo.getCardNumber())
                    .bankName(cardInfo.getBankName())
                    .cardHolderName(cardInfo.getCardHolderName())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpiInfoModel {
        private String id;
        private String upiId;

        public static UpiInfoModel buildFrom(@Nullable UpiInfo upiInfo) {
            if (upiInfo == null) return null;

            return UpiInfoModel.builder()
                    .id(upiInfo.getId())
                    .upiId(upiInfo.getUpiId())
                    .build();
        }
    }

    private String id;
    private ZonedDateTime createdOn;
    private ZonedDateTime lastModifiedOn;
    private PaymentMode paymentMode;
    private BigDecimal amount;
    private String currency;
    private CardInfoModel cardInfo;
    private UpiInfoModel upiInfo;
    private PaymentStatus paymentStatus;
    private String failureReason;

    public static PaymentModel buildFrom(@Nullable Payment payment) {
        if (payment == null) return null;
        return PaymentModel.builder()
                .id(payment.getId())
                .createdOn(payment.getCreatedOn())
                .lastModifiedOn(payment.getLastModifiedOn())
                .paymentMode(payment.getPaymentMode())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .cardInfo(CardInfoModel.buildFrom(payment.getCardInfo()))
                .upiInfo(UpiInfoModel.buildFrom(payment.getUpiInfo()))
                .paymentStatus(payment.getPaymentStatus())
                .failureReason(payment.getFailureReason())
                .build();
    }
}
