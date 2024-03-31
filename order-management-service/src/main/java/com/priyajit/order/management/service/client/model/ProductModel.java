package com.priyajit.order.management.service.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceModel {
        private BigDecimal price;
        private String currencyName;
    }

    private String id;
    private String title;
    private PriceModel price;
}
