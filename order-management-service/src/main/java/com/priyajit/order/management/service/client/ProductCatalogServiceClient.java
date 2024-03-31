package com.priyajit.order.management.service.client;

import com.priyajit.order.management.service.client.model.ProductModel;

import java.util.Optional;

public interface ProductCatalogServiceClient {
    Optional<ProductModel> findProductByProductId(String productId);
}
