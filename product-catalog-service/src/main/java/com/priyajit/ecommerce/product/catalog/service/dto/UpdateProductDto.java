package com.priyajit.ecommerce.product.catalog.service.dto;

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
public class UpdateProductDto {

    private String productId;
    private String title;
    private BigDecimal price;
    private String currencyId;
    private String description;
    private List<String> taggedCategoryIds;
    private List<String> productImageIds;
}
