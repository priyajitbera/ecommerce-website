package com.priyajit.product.ecommerce.catalog.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProductDto {

    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;
}
