package com.priyajit.order.management.service.client.impl;

import com.priyajit.order.management.service.client.ProductCatalogServiceClient;
import com.priyajit.order.management.service.client.model.ProductModel;
import com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration.Keys;

@Slf4j
@Component
public class ProductCatalogServiceClientImplV1 implements ProductCatalogServiceClient {

    private DbEnvironmentConfiguration configuration;

    public ProductCatalogServiceClientImplV1(DbEnvironmentConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Optional<ProductModel> findProductByProductId(String productId) {

        var BASE_URL = configuration.getProperty(Keys.PRODUCT_CATALOG_SERVICE_BASE_URL);
        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();

        ResponseEntity<ProductModel> response;
        try {
            log.info("Before calling product-catalog-service API");
            response = restClient.get().uri(uri -> uri
                            .path("/product/v1/get")
                            .queryParam("productId", productId)
                            .build())
                    .retrieve()
                    .toEntity(ProductModel.class);
            log.info("After calling product-catalog-service API, status:{}", response.getStatusCode());
        }
        // error
        catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
                return Optional.empty();
            } else {
                throw new RuntimeException("Error occurred while calling product-catalog-service API ", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while calling product-catalog-service API ", e);
        }
        if (HttpStatus.OK == response.getStatusCode()) {
            var productModel = response.getBody();
            // validate response body content
            if (productModel == null) {
                return Optional.empty();
            }
            return Optional.of(productModel);
        } else if (HttpStatus.NOT_FOUND == response.getStatusCode()) {
            return Optional.empty();
        } else throw new RuntimeException("Error occurred while calling product-catalog-service API");
    }
}
