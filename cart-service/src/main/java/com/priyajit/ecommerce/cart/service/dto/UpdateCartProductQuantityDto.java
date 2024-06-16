package com.priyajit.ecommerce.cart.service.dto;

import com.priyajit.ecommerce.cart.service.domain.CartProductOperation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartProductQuantityDto {

    @NotBlank
    private String cartId;

    @NotBlank
    private String productId;

    @NotNull
    @Min(1)
    private Long quantity;

    @NotNull
    private CartProductOperation operation;
}
