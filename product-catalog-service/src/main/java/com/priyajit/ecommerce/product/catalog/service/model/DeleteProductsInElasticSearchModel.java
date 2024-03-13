package com.priyajit.ecommerce.product.catalog.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProductsInElasticSearchModel {
    private List<String> productIds;
}
