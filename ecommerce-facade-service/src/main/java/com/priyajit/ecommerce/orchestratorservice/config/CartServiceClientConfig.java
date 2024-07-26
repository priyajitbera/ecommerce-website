package com.priyajit.ecommerce.orchestratorservice.config;

import com.priyajit.ecommerce.cs.ApiClient;
import com.priyajit.ecommerce.cs.api.CartControllerV1Api;
import com.priyajit.ecommerce.orchestratorservice.config.properties.CartServiceApiClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CartServiceClientConfig {

    @Bean("cartServiceApiClient")
    public ApiClient cartServiceApiClient(CartServiceApiClientProperties properties) {
        log.info("Configuring cartServiceApiClient baseUrl: {}", properties.getBaseUrl());
        var apiClient = new ApiClient();
        apiClient.setBasePath(properties.getBaseUrl());
        return apiClient;
    }

    @Bean
    public CartControllerV1Api cartControllerV1Api(
            @Qualifier("cartServiceApiClient")
            ApiClient apiClient
    ) {
        return new CartControllerV1Api(apiClient);
    }
}
