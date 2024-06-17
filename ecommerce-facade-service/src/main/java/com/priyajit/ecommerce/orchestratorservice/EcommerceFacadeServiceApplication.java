package com.priyajit.ecommerce.orchestratorservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyajit.ecommerce.orchestratorservice.config.properties.CartServiceApiClientProperties;
import com.priyajit.ecommerce.orchestratorservice.config.properties.ProductCatalogServiceApiClientProperties;
import com.priyajit.ecommerce.orchestratorservice.config.properties.UserManagementServiceApiClientProperties;
import com.priyajit.ecommerce.orchestratorservice.config.properties.UserTokenParserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        UserTokenParserProperties.class,
        ProductCatalogServiceApiClientProperties.class,
        UserManagementServiceApiClientProperties.class,
        CartServiceApiClientProperties.class
})
public class EcommerceFacadeServiceApplication {

    @Autowired
    private ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(EcommerceFacadeServiceApplication.class, args);
    }

}
