package com.priyajit.ecommerce.cart.service.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindProductsResponseModel {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductModel {
        private String id;
        private String title;
    }

    private List<ProductModel> products;
}
