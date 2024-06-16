package com.priyajit.ecommerce.cart.service.config;


import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.product.catalog.service.ApiClient;
import com.priyajit.ecommerce.product.catalog.service.api.CurrencyControllerV1Api;
import com.priyajit.ecommerce.product.catalog.service.api.ProductCategoryControllerV1Api;
import com.priyajit.ecommerce.product.catalog.service.api.ProductControllerV1Api;
import com.priyajit.ecommerce.product.catalog.service.api.ProductImageControllerV1Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration.Keys.PRODUCT_CATALOG_SERVICE_BASE_URL;

@Slf4j
@Configuration
public class ProductCatalogServiceClientConfig {

    @Bean("productCatalogServiceApiClient")
    public ApiClient productCatalogServiceApiClient(DbEnvironmentConfiguration configuration) {
        log.info("Configuring productCatalogServiceApiClient baseUrl: {}", configuration.getProperty(PRODUCT_CATALOG_SERVICE_BASE_URL));
        var apiClient = new ApiClient();
        apiClient.setBasePath(configuration.getProperty(PRODUCT_CATALOG_SERVICE_BASE_URL));
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
