package com.priyajit.example.ecommerce_oauth2_authorization_server;

import com.priyajit.example.ecommerce_oauth2_authorization_server.config.AuthTokenProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AuthTokenProperties.class)
@SpringBootApplication
public class EcommerceOauth2AuthorizationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceOauth2AuthorizationServerApplication.class, args);
    }

}
