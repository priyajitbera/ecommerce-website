package com.priyajit.ecommerce.cart.service.client;

import com.priyajit.ecommerce.cart.service.client.model.FindProductsResponseModel;

import java.util.Optional;

public interface ProductCatalogServiceClient {
    Optional<FindProductsResponseModel.ProductModel> findProductByProductId(String productId);
}
