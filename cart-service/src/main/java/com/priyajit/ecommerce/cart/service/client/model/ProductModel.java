package com.priyajit.ecommerce.cart.service.client.model;

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
public class ProductModel {

    private String id;
    private String title;
    private PriceModel price;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceModel {
        private BigDecimal price;
        private CurrencyModel currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyModel {
        private String id;
        private ZonedDateTime createdOn;
        private ZonedDateTime lastModifiedOn;
        private String name;
        private String symbol;
        private String shortSymbol;
    }
}
