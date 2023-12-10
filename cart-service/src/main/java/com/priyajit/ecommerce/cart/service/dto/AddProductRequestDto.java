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

    private Long cartId;
    private Long productId;
    private Long quantity;
}
