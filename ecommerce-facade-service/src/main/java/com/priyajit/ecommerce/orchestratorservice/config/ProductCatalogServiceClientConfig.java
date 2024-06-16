package com.priyajit.ecommerce.orchestratorservice.config;


import com.priyajit.ecommerce.orchestratorservice.config.properties.ProductCatalogServiceApiClientProperties;
import com.priyajit.ecommerce.product_catalog_service.ApiClient;
import com.priyajit.ecommerce.product_catalog_service.api.CurrencyControllerV1Api;
import com.priyajit.ecommerce.product_catalog_service.api.ProductCategoryControllerV1Api;
import com.priyajit.ecommerce.product_catalog_service.api.ProductControllerV1Api;
import com.priyajit.ecommerce.product_catalog_service.api.ProductImageControllerV1Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProductCatalogServiceClientConfig {

    @Bean("productCatalogServiceApiClient")
    public ApiClient productCatalogServiceApiClient(ProductCatalogServiceApiClientProperties properties) {
        log.info("Configuring productCatalogServiceApiClient baseUrl: {}", properties.getBaseUrl());
        var apiClient = new ApiClient();
        apiClient.setBasePath(properties.getBaseUrl());
        return apiClient;
    }

    @Bean
    public ProductControllerV1Api productControllerV1Api(
            @Qualifier("productCatalogServiceApiClient")
            ApiClient apiClient
    ) {
        return new ProductControllerV1Api(apiClient);
    }

    @Bean
    public CurrencyControllerV1Api currencyControllerV1Api(
            @Qualifier("productCatalogServiceApiClient")
            ApiClient apiClient
    ) {
        return new CurrencyControllerV1Api(apiClient);
    }

    @Bean
    public ProductCategoryControllerV1Api productCategoryControllerV1Api(
            @Qualifier("productCatalogServiceApiClient")
            ApiClient apiClient
    ) {
        return new ProductCategoryControllerV1Api(apiClient);
    }

    @Bean
    public ProductImageControllerV1Api productImageControllerV1Api(
            @Qualifier("productCatalogServiceApiClient")
            ApiClient apiClient
    ) {
        return new ProductImageControllerV1Api(apiClient);
    }
}
