package com.priyajit.example.ecommerce_oauth2_authorization_server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth-token")
public class AuthTokenProperties {

    private String username;
    private String password;
    private String privateKeyFilePath;
}
