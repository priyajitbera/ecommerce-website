package com.priyajit.ecommerce.cart.service.client.impl;

import com.priyajit.ecommerce.cart.service.client.ProductCatalogServiceClient;
import com.priyajit.ecommerce.cart.service.client.model.FindProductsResponseModel;
import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration.Keys;

@Slf4j
@Component
public class ProductCatalogServiceClientImplV1 implements ProductCatalogServiceClient {

    private DbEnvironmentConfiguration configuration;

    public ProductCatalogServiceClientImplV1(DbEnvironmentConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Optional<FindProductsResponseModel.ProductModel> findProductByProductId(String productId) {

        var BASE_URL = configuration.getProperty(Keys.PRODUCT_CATALOG_SERVICE_BASE_URL);
        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();

        ResponseEntity<FindProductsResponseModel> response;
        try {
            log.info("Before calling product-catalog-service API");
            response = restClient.get().uri(uri -> uri
                            .path("/product/v1")
                            .queryParam("productIds", productId)
                            .build())
                    .retrieve()
                    .toEntity(FindProductsResponseModel.class);
            log.info("After calling product-catalog-service API, status:{}", response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while calling product-catalog-service API ", e);
        }

        if (HttpStatus.OK == response.getStatusCode()) {
            var responseBody = response.getBody();
            // validate response body content
            if (responseBody == null || responseBody.getProducts() == null || responseBody.getProducts().size() == 0) {
                return Optional.empty();
            }
            return Optional.of(responseBody.getProducts().get(0));
        } else if (HttpStatus.NOT_FOUND == response.getStatusCode()) {
            return Optional.empty();
        } else throw new RuntimeException("Error occurred while calling product-catalog-service API");
    }
}
