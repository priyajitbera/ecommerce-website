package com.priyajit.ecommerce.facade.service.config;


import com.priyajit.ecommerce.facade.service.config.properties.UserManagementServiceApiClientProperties;
import com.priyajit.ecommerce.ums.ApiClient;
import com.priyajit.ecommerce.ums.api.AuthControllerV1Api;
import com.priyajit.ecommerce.ums.api.UserControllerV1Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class UserManagementServiceClientConfig {

    @Bean("userManagementServiceApiClient")
    public ApiClient userManagementServiceApiClient(UserManagementServiceApiClientProperties properties) {
        log.info("Configuring userManagementServiceApiClient baseUrl: {}", properties.getBaseUrl());
        var apiClient = new ApiClient();
        apiClient.setBasePath(properties.getBaseUrl());
        return apiClient;
    }

    @Bean
    public UserControllerV1Api userControllerV1Api(
            @Qualifier("userManagementServiceApiClient")
            ApiClient apiClient
    ) {
        return new UserControllerV1Api(apiClient);
    }

    @Bean
    public AuthControllerV1Api authControllerV1Api(
            @Qualifier("userManagementServiceApiClient")
            ApiClient apiClient
    ) {
        return new AuthControllerV1Api(apiClient);
    }
}
