package com.priyajit.ecommerce.cart.service.client;

import com.priyajit.ecommerce.cart.service.client.model.ProductModel;

import java.util.Optional;

public interface ProductCatalogServiceClient {
    Optional<ProductModel> findProductByProductId(String productId);
}
