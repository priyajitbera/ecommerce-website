package com.priyajit.ecommerce.cart.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartModel {

    private Long cartId;
    private Long userId;
    private List<CartProductModel> products = new ArrayList<>();
}
