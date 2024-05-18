package com.priyajit.ecommerce.product.catalog.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductDto {

    @NotBlank
    private String title;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @NotBlank
    private String currencyId;

    @NotBlank
    private String description;

    @NotEmpty
    private List<String> taggedCategoryIds;

    private List<String> productImageIds;
}
