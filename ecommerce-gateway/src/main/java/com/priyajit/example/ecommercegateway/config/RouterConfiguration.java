package com.priyajit.example.ecommercegateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RouterConfiguration {

    private final String PRODUCT_SERVICE_BASE_PATH = "/product-catalog-service";
    private final String USER_SERVICE_BASE_PATH = "/user-management-service";
    private final String CART_SERVICE_BASE_PATH = "/cart-service";
    private final String ORDER_SERVICE_BASE_PATH = "/order-service";
    private final String PAYMENT_SERVICE_BASE_PATH = "/payment-service";

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(routeSpec -> routeSpec
                        .path(PRODUCT_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(PRODUCT_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8081")
                )
                .route(routeSpec -> routeSpec
                        .path(USER_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(USER_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8082")
                )
                .route(routeSpec -> routeSpec
                        .path(CART_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(CART_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8083")
                )
                .route(routeSpec -> routeSpec
                        .path(ORDER_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(ORDER_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8084")
                )
                .route(routeSpec -> routeSpec
                        .path(PAYMENT_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(PAYMENT_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8085")
                )
                .build();
    }
}
