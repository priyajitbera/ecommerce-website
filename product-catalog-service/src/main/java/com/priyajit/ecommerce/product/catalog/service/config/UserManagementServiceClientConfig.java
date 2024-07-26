package com.priyajit.ecommerce.product.catalog.service.config;


import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.user_management_service.ApiClient;
import com.priyajit.ecommerce.user_management_service.api.AuthControllerV1Api;
import com.priyajit.ecommerce.user_management_service.api.UserControllerV1Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class UserManagementServiceClientConfig {

    @Bean("userManagementServiceApiClient")
    public ApiClient userManagementServiceApiClient(DbEnvironmentConfiguration configuration) {

        var baseUrl = configuration.getProperty(DbEnvironmentConfiguration.Keys.USER_MANAGEMENT_SERVICE_BASE_URL);
        log.info("Configuring userManagementServiceApiClient baseUrl: {}", baseUrl);
        var apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);
        return apiClient;
    }

    @Bean
    public UserControllerV1Api userControllerV1Api(
            @Qualifier("userManagementServiceApiClient")
            ApiClient apiClient
    ) {
        return new UserControllerV1Api(apiClient);
    }
}
