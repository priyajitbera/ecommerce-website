package com.priyajit.ecommerce.cart.service.mogodoc;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {

    private String productId;
    private Long quantity;
}
