package com.priyajit.ecommerce.product.catalog.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProductsInElasticSearchDto {
    private List<String> productIds;
}
