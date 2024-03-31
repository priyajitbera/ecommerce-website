package com.priyajit.order.management.service.dto;

import com.priyajit.order.management.service.domain.CardType;
import com.priyajit.order.management.service.domain.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDto {
        private String productId;
        private Long quantity;
        private BigDecimal productUnitPrice;
        private BigDecimal orderItemValue; // (quantity * productUnitPrice)
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryAddressDto {
        private String buildingNumber;
        private String houseNumber;
        private String addressLine1;
        private String addressLine2;
        private String landMark;
        private String contactNumber1;
        private String contactNumber2;
        private String city;
        private Integer pincode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentInfo {
        private PaymentMode paymentMode;
        private CardInfo cardInfo;
        private UpiInfo upiInfo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardInfo {
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
    public static class UpiInfo {
        private String upiId;
    }

    private String userId;
    private List<OrderItemDto> orderItems;
    private BigDecimal orderTotal;
    private DeliveryAddressDto deliveryAddress;
    private PaymentInfo paymentInfo;
}
