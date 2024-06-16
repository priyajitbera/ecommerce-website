package com.priyajit.ecommerce.orchestratorservice.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cart-service")
@AllArgsConstructor
@Getter
public class CartServiceApiClientProperties {

    private String baseUrl;
}
