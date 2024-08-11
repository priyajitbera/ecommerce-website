package com.priyajit.example.ecommercegateway;

import com.priyajit.example.ecommercegateway.config.AuthTokenProperties;
import com.priyajit.example.ecommercegateway.config.properties.CartServiceApiClientProperties;
import com.priyajit.example.ecommercegateway.config.properties.OrderManagementServiceApiClientProperties;
import com.priyajit.example.ecommercegateway.config.properties.ProductCatalogServiceApiClientProperties;
import com.priyajit.example.ecommercegateway.config.properties.UserManagementServiceApiClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        AuthTokenProperties.class,
        CartServiceApiClientProperties.class,
        OrderManagementServiceApiClientProperties.class,
        ProductCatalogServiceApiClientProperties.class,
        UserManagementServiceApiClientProperties.class
})
@SpringBootApplication
public class EcommerceGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceGatewayApplication.class, args);
    }

}
