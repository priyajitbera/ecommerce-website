package com.priyajit.order.management.service.mongodoc;

import com.priyajit.order.management.service.domain.OrderStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    private String id;
    private LocalDateTime createdOn;
    private LocalDateTime lastModifiedOn;
    private String userId;
    private List<OrderItem> orderItems;
    private BigDecimal orderTotal;
    private PaymentInfo paymentInfo;
    private OrderStatus orderStatus;
    private DeliveryInfo deliveryInfo;
}
