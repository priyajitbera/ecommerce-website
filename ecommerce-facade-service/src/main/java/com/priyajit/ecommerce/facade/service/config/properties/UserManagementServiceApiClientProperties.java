package com.priyajit.ecommerce.facade.service.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user-management-service")
@AllArgsConstructor
@Getter
public class UserManagementServiceApiClientProperties {

    private String baseUrl;
}
