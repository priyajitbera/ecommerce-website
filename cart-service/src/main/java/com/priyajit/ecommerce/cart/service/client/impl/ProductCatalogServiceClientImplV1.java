package com.priyajit.ecommerce.cart.service.client.impl;

import com.priyajit.ecommerce.cart.service.client.ProductCatalogServiceClient;
import com.priyajit.ecommerce.cart.service.client.model.ProductModel;
import com.priyajit.ecommerce.cart.service.client.model.Response;
import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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
    public Optional<ProductModel> findProductByProductId(String productId) {

        var BASE_URL = configuration.getProperty(Keys.PRODUCT_CATALOG_SERVICE_BASE_URL);
        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();

        ResponseEntity<Response<ProductModel>> response;
        try {
            log.info("Before calling product-catalog-service API");
            response = restClient.get().uri(uri -> uri
                            .path("/product/v1/get")
                            .queryParam("productId", productId)
                            .build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Response<ProductModel>>() {
                    });
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

        // log the error present in response body (if provided)
        if (response.getBody() != null && response.getBody().getError() != null) {
            log.error("Provided error in response body: {}", response.getBody().getError());
        }
        if (HttpStatus.OK == response.getStatusCode() && response.getBody() != null && response.getBody().getData() != null) {
            var productModel = response.getBody().getData();
            return Optional.of(productModel);

        } else if (HttpStatus.NOT_FOUND == response.getStatusCode()) {
            return Optional.empty();
        } else if (HttpStatus.OK != response.getStatusCode()) {
            throw new RuntimeException(String.format("Error occurred while calling product-catalog-service API, expected status code: %s but got: %s",
                    HttpStatus.OK, response.getStatusCode()));
        } else if (response.getBody() == null) {
            throw new RuntimeException("Error occurred while calling product-catalog-service API, expected non null value in response body");
        } else {
            throw new RuntimeException("Error occurred while calling product-catalog-service API, expected non null value in response body.data");
        }
    }
}
