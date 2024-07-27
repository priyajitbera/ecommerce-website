package com.priyajit.ecommerce.facade.service.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2-authorization-service")
@AllArgsConstructor
@Getter
public class OAuth2AuthorizationServiceApiClientProperties {

    private String baseUrl;
    private String clientId;
    private String secret;
}
