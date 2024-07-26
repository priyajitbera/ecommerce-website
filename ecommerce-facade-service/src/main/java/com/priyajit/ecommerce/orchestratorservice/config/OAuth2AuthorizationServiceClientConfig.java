package com.priyajit.ecommerce.orchestratorservice.config;

import com.priyajit.ecommerce.oas.ApiClient;
import com.priyajit.ecommerce.oas.api.OAuth2ControllerApi;
import com.priyajit.ecommerce.orchestratorservice.config.properties.OAuth2AuthorizationServiceApiClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class OAuth2AuthorizationServiceClientConfig {
    private final static String BASIC_PREFIX = "Basic ";

    @Bean("oAuth2AuthorizationServiceApiClient")
    public ApiClient oAuth2AuthorizationServiceApiClient(OAuth2AuthorizationServiceApiClientProperties properties) {
        log.info("Configuring oAuth2AuthorizationServiceApiClientProperties baseUrl: {}", properties.getBaseUrl());
        var apiClient = new ApiClient();
        apiClient.setBasePath(properties.getBaseUrl());

        var base64EncodedCredentials = HttpHeaders.encodeBasicAuth(
                properties.getClientId(),
                properties.getSecret(),
                StandardCharsets.UTF_8
        );

        apiClient.addDefaultHeader(
                HttpHeaders.AUTHORIZATION,
                BASIC_PREFIX + base64EncodedCredentials
        );

        return apiClient;
    }

    @Bean("oAuth2ControllerApi")
    public OAuth2ControllerApi oAuth2ControllerApi(
            @Qualifier("oAuth2AuthorizationServiceApiClient")
            ApiClient apiClient
    ) {
        return new OAuth2ControllerApi(apiClient);
    }
}
