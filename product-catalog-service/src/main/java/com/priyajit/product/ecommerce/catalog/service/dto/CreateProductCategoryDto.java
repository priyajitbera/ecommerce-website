package com.priyajit.product.ecommerce.catalog.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCategoryDto {

    private String name;
    private String parentCategoryId;
}
