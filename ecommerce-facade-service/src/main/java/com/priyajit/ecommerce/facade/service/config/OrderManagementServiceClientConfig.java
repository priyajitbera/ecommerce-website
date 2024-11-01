package com.priyajit.ecommerce.facade.service.config;


import com.priyajit.ecommerce.facade.service.config.properties.OrderManagementServiceApiClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OrderManagementServiceClientConfig {

    @Bean("orderManagementServiceApiClient")
    public com.priyajit.ecommerce.oms.ApiClient cartServiceApiClient(OrderManagementServiceApiClientProperties properties) {
        log.info("Configuring orderManagementServiceApiClient baseUrl: {}", properties.getBaseUrl());
        var apiClient = new com.priyajit.ecommerce.oms.ApiClient();
        apiClient.setBasePath(properties.getBaseUrl());
        return apiClient;
    }

    @Bean
    public com.priyajit.ecommerce.oms.api.OrderControllerV1Api orderControllerV1Api(
            @Qualifier("orderManagementServiceApiClient")
            com.priyajit.ecommerce.oms.ApiClient apiClient
    ) {
        return new com.priyajit.ecommerce.oms.api.OrderControllerV1Api(apiClient);
    }
}
