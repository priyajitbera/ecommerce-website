package com.priyajit.ecommerce.cart.service.dto;

import com.priyajit.ecommerce.cart.service.domain.CartProductOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartProductQuantityDto {

    private String cartId;
    private String productId;
    private Long quantity;
    private CartProductOperation operation;
}
