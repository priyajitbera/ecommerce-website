package com.priyajit.ecommerce.cart.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProductModel {

    private String productId;
    private Long quantity;
}
