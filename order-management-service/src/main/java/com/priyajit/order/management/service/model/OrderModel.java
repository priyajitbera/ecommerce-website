package com.priyajit.order.management.service.model;

import com.priyajit.order.management.service.domain.CardType;
import com.priyajit.order.management.service.domain.PaymentMode;
import com.priyajit.order.management.service.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemModel {
        private String productId;
        private String productTitle;
        private Long quantity;
        private BigDecimal productUnitPrice;
        private BigDecimal orderItemValue; // (quantity * productUnitPrice)
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryAddressModel {

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
    public static class PaymentInfoModel {

        private PaymentMode paymentMode;
        private PaymentStatus paymentStatus;
        private CardInfoModel cardInfo;
        private UpiInfoModel upiInfo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardInfoModel {
        private CardType cardType;
        private String cardNumber;
        private String bankName;
        private String cardHolderName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpiInfoModel {
        private String upiId;
    }

    private String orderId;
    private ZonedDateTime createdOn;
    private String userId;
    private List<OrderItemModel> orderItems;
    private BigDecimal orderTotal;
    private DeliveryAddressModel deliveryAddress;
    private PaymentInfoModel paymentInfo;
}
