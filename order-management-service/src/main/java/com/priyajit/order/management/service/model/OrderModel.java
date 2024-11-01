package com.priyajit.order.management.service.model;

import com.priyajit.order.management.service.domain.*;
import com.priyajit.order.management.service.mongodoc.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {

    private String orderId;
    private ZonedDateTime createdOn;
    private String userId;
    private List<OrderItemModel> orderItems;
    private BigDecimal orderTotal;
    private DeliveryInfoModel deliveryInfo;
    private PaymentInfoModel paymentInfo;
    private OrderStatus orderStatus;

    public static List<OrderModel> buildFrom(@Nullable Collection<Order> orders) {
        if (orders == null) return null;

        return orders.stream().map(OrderModel::buildFrom).collect(Collectors.toList());
    }

    public static OrderModel buildFrom(@Nullable Order order) {
        if (order == null) return null;

        return OrderModel.builder()
                .orderId(order.getId())
                .createdOn(order.getCreatedOn().atZone(ZoneId.of("UTC")))
                .userId(order.getUserId())
                .orderItems(OrderItemModel.buildFrom(order.getOrderItems()))
                .orderTotal(order.getOrderTotal())
                .deliveryInfo(DeliveryInfoModel.buildFrom(order.getDeliveryInfo()))
                .paymentInfo(PaymentInfoModel.buildFrom(order.getPaymentInfo()))
                .orderStatus(order.getOrderStatus())
                .build();
    }

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

        public static List<OrderItemModel> buildFrom(@Nullable Collection<OrderItem> orderItems) {
            if (orderItems == null) return null;

            return orderItems.stream().map(OrderItemModel::buildFrom).collect(Collectors.toList());
        }

        public static OrderItemModel buildFrom(@Nullable OrderItem orderItem) {
            if (orderItem == null) return null;

            return OrderModel.OrderItemModel.builder()
                    .productId(orderItem.getProductId())
                    .productTitle(orderItem.getProductTitle())
                    .quantity(orderItem.getQuantity())
                    .productUnitPrice(orderItem.getProductUnitPrice())
                    .orderItemValue(orderItem.getOrderItemValue())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryInfoModel {

        private DeliveryStatus deliveryStatus;
        private List<DeliveryUpdateModel> deliveryUpdates;
        private DeliveryAddressModel deliveryAddress;

        public static DeliveryInfoModel buildFrom(DeliveryInfo deliveryInfo) {
            if (deliveryInfo == null) return null;

            return DeliveryInfoModel.builder()
                    .deliveryStatus(deliveryInfo.getDeliveryStatus())
                    .deliveryUpdates(DeliveryUpdateModel.buildFrom(deliveryInfo.getDeliveryUpdates()))
                    .deliveryAddress(DeliveryAddressModel.buildFrom(deliveryInfo.getDeliveryAddress()))
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryUpdateModel {
        private ZonedDateTime timeStamp;
        private String message;

        public static List<DeliveryUpdateModel> buildFrom(List<DeliveryUpdate> deliveryUpdates) {
            if (deliveryUpdates == null) return null;

            return deliveryUpdates.stream()
                    .map(DeliveryUpdateModel::buildFrom)
                    .collect(Collectors.toList());
        }

        public static DeliveryUpdateModel buildFrom(DeliveryUpdate deliveryUpdate) {
            if (deliveryUpdate == null) return null;

            return DeliveryUpdateModel.builder()
                    .timeStamp(deliveryUpdate.getTimeStamp().atZone(ZoneId.of("UTC")))
                    .message(deliveryUpdate.getMessage())
                    .build();
        }
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

        public static DeliveryAddressModel buildFrom(@Nullable DeliveryAddress deliveryAddress) {
            if (deliveryAddress == null) return null;

            return OrderModel.DeliveryAddressModel.builder()
                    .buildingNumber(deliveryAddress.getBuildingNumber())
                    .houseNumber(deliveryAddress.getHouseNumber())
                    .addressLine1(deliveryAddress.getAddressLine1())
                    .addressLine2(deliveryAddress.getAddressLine2())
                    .landMark(deliveryAddress.getLandMark())
                    .contactNumber1(deliveryAddress.getContactNumber1())
                    .contactNumber2(deliveryAddress.getContactNumber2())
                    .city(deliveryAddress.getCity())
                    .pincode(deliveryAddress.getPincode())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentInfoModel {

        private String paymentId;
        private PaymentMode paymentMode;
        private PaymentStatus paymentStatus;
        private CardInfoModel cardInfo;
        private UpiInfoModel upiInfo;
        private String paymentGatewayLink;

        public static PaymentInfoModel buildFrom(@Nullable PaymentInfo paymentInfo) {
            if (paymentInfo == null) return null;

            return OrderModel.PaymentInfoModel.builder()
                    .paymentId(paymentInfo.getPaymentId())
                    .paymentStatus(paymentInfo.getPaymentStatus())
                    .paymentMode(paymentInfo.getPaymentMode())
                    .build();
        }
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
}
