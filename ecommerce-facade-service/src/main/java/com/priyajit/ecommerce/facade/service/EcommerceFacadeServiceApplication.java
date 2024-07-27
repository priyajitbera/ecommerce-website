package com.priyajit.ecommerce.facade.service;

import com.priyajit.ecommerce.facade.service.config.properties.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        UserTokenParserProperties.class,
        ProductCatalogServiceApiClientProperties.class,
        UserManagementServiceApiClientProperties.class,
        CartServiceApiClientProperties.class,
        OAuth2AuthorizationServiceApiClientProperties.class
})
public class EcommerceFacadeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceFacadeServiceApplication.class, args);
    }
}