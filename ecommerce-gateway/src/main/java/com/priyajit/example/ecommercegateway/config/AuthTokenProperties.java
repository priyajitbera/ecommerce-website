package com.priyajit.example.ecommercegateway.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "auth-token")
public class AuthTokenProperties {

    String publicKeyFilePath;
}
