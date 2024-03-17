package com.priyajit.ecommerce.cart.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequestDto {

    private String cartId;
    private String productId;
    private Long quantity;
}
