package com.priyajit.ecommerce.cart.service.client;

import com.priyajit.ecommerce.cart.service.client.model.CurrencyModel;
import com.priyajit.ecommerce.cart.service.client.model.ProductModel;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface ProductCatalogServiceClient {

    Optional<ProductModel> findProductByProductId(String productId);

    List<CurrencyModel> findCurrencies(
            @Nullable List<String> ids,
            @Nullable List<String> name
    );
}
