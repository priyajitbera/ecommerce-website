package com.priyajit.example.ecommercegateway.config;

import com.priyajit.example.ecommercegateway.config.properties.CartServiceApiClientProperties;
import com.priyajit.example.ecommercegateway.config.properties.OrderManagementServiceApiClientProperties;
import com.priyajit.example.ecommercegateway.config.properties.ProductCatalogServiceApiClientProperties;
import com.priyajit.example.ecommercegateway.config.properties.UserManagementServiceApiClientProperties;
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
    private final String ORDER_SERVICE_BASE_PATH = "/order-management-service";
    private final String PAYMENT_SERVICE_BASE_PATH = "/payment-service";

    private CartServiceApiClientProperties cartServiceApiClientProperties;
    private OrderManagementServiceApiClientProperties orderManagementServiceApiClientProperties;
    private ProductCatalogServiceApiClientProperties productCatalogServiceApiClientProperties;
    private UserManagementServiceApiClientProperties userManagementServiceApiClientProperties;

    public RouterConfiguration(
            CartServiceApiClientProperties cartServiceApiClientProperties,
            OrderManagementServiceApiClientProperties orderManagementServiceApiClientProperties,
            ProductCatalogServiceApiClientProperties productCatalogServiceApiClientProperties,
            UserManagementServiceApiClientProperties userManagementServiceApiClientProperties
    ) {
        this.cartServiceApiClientProperties = cartServiceApiClientProperties;
        this.orderManagementServiceApiClientProperties = orderManagementServiceApiClientProperties;
        this.productCatalogServiceApiClientProperties = productCatalogServiceApiClientProperties;
        this.userManagementServiceApiClientProperties = userManagementServiceApiClientProperties;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(routeSpec -> routeSpec
                        .path(PRODUCT_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(PRODUCT_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri(productCatalogServiceApiClientProperties.getBaseUrl())
                )
                .route(routeSpec -> routeSpec
                        .path(USER_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(USER_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri(userManagementServiceApiClientProperties.getBaseUrl())
                )
                .route(routeSpec -> routeSpec
                        .path(CART_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(CART_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri(cartServiceApiClientProperties.getBaseUrl())
                )
                .route(routeSpec -> routeSpec
                        .path(ORDER_SERVICE_BASE_PATH + "/**")
                        .filters(uriSpec -> uriSpec
                                .rewritePath(ORDER_SERVICE_BASE_PATH + "/(?<segment>.*)", "/${segment}"))
                        .uri(orderManagementServiceApiClientProperties.getBaseUrl())
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
