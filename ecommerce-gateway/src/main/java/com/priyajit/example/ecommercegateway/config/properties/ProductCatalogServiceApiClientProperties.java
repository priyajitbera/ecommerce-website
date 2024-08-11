package com.priyajit.example.ecommercegateway.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("product-catalog-service")
@AllArgsConstructor
@Getter
public class ProductCatalogServiceApiClientProperties {

    private String baseUrl;
}
