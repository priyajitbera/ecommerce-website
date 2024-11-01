package com.priyajit.ecommerce.facade.service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Aspect
@Component
public class ApiClientAspect {

    private final static String BEARER_PREFIX = "Bearer ";

    private com.priyajit.ecommerce.pcs.ApiClient productCatalogServiceApiClient;
    private com.priyajit.ecommerce.ums.ApiClient userManagementServiceApiClient;
    private com.priyajit.ecommerce.cs.ApiClient cartServiceApiClient;
    private com.priyajit.ecommerce.oms.ApiClient orderManagementServiceApiClient;
    private com.priyajit.ecommerce.oas.api.OAuth2ControllerApi oAuth2ControllerApi;

    public ApiClientAspect(
            com.priyajit.ecommerce.pcs.ApiClient productCatalogServiceApiClient,
            com.priyajit.ecommerce.ums.ApiClient userManagementServiceApiClient,
            com.priyajit.ecommerce.cs.ApiClient cartServiceApiClient,
            com.priyajit.ecommerce.oms.ApiClient orderManagementServiceApiClient,
            com.priyajit.ecommerce.oas.api.OAuth2ControllerApi oAuth2ControllerApi
    ) {
        this.productCatalogServiceApiClient = productCatalogServiceApiClient;
        this.userManagementServiceApiClient = userManagementServiceApiClient;
        this.cartServiceApiClient = cartServiceApiClient;
        this.orderManagementServiceApiClient = orderManagementServiceApiClient;
        this.oAuth2ControllerApi = oAuth2ControllerApi;
    }

    @Before("execution(* com.priyajit.ecommerce.pcs.api.*.*(..))")
    public void fetchAndUpdateBearerTokenForProductCatalogServiceApiClient() {
        productCatalogServiceApiClient.addDefaultHeader(
                HttpHeaders.AUTHORIZATION,
                BEARER_PREFIX + fetchTokenResponseSchema().getAccessToken()
        );
        log.info("Updated Bearer token for productCatalogServiceApiClient");
    }

    @Before("execution(*  com.priyajit.ecommerce.ums.api.*.*(..))")
    public void fetchAndUpdateBearerTokenForUserManagementServiceApiClient() {
        userManagementServiceApiClient.addDefaultHeader(
                HttpHeaders.AUTHORIZATION,
                BEARER_PREFIX + fetchTokenResponseSchema().getAccessToken()
        );
        log.info("Updated Bearer token for userManagementServiceApiClient");
    }

    @Before("execution(* com.priyajit.ecommerce.cs.api.*.*(..))")
    public void fetchAndUpdateBearerTokenForCartServiceApiClient() {
        cartServiceApiClient.addDefaultHeader(
                HttpHeaders.AUTHORIZATION,
                BEARER_PREFIX + fetchTokenResponseSchema().getAccessToken()
        );
        log.info("Updated Bearer token for cartServiceApiClient");
    }

    @Before("execution(* com.priyajit.ecommerce.oms.api.*.*(..))")
    public void fetchAndUpdateBearerTokenForOrderManagementServiceApiClient() {
        orderManagementServiceApiClient.addDefaultHeader(
                HttpHeaders.AUTHORIZATION,
                BEARER_PREFIX + fetchTokenResponseSchema().getAccessToken()
        );
        log.info("Updated Bearer token for orderManagementServiceApiClient");
    }

    private com.priyajit.ecommerce.oas.model.FetchTokenResponseSchema fetchTokenResponseSchema() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Before calling oAuth2ControllerApi.fetchToken");
                var response = oAuth2ControllerApi.fetchToken(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())
                        .block(); // use block as Auth2ControllerApi.fetchToken is reactive
                log.info("After calling oAuth2ControllerApi.fetchToken success");
                return response;
            } catch (Throwable e) {
                log.error("After calling oAuth2ControllerApi.fetchToken error occurred, msg: {}", e.getMessage());
                throw e;
            }
        }).join();
    }
}
