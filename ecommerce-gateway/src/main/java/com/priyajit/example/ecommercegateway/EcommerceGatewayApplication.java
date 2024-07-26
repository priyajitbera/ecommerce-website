package com.priyajit.example.ecommercegateway;

import com.priyajit.example.ecommercegateway.config.AuthTokenProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AuthTokenProperties.class)
@SpringBootApplication
public class EcommerceGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceGatewayApplication.class, args);
    }

}
