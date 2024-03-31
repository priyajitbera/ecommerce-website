package com.priyajit.order.management.service.mongodoc;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {

    private String productId;
    private Long quantity;
    private BigDecimal productUnitPrice;
    private BigDecimal orderItemValue; // (quantity * productUnitPrice)
}
