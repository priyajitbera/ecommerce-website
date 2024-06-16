package com.priyajit.ecommerce.cart.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartWithValueModel {

    private String cartId;
    private String userId;
    private List<CartProductModel> products;
    private BigDecimal cartValue;
    private String currency;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartProductModel {

        private String productId;
        private String productTitle;
        private Long quantity;
        private BigDecimal price;
    }
}
