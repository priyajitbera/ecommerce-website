package com.priyajit.ecommerce.orchestratorservice.controller;

import com.priyajit.ecommerce.cs.api.CartControllerV1Api;
import com.priyajit.ecommerce.fs.api.CartServiceApi;
import com.priyajit.ecommerce.orchestratorservice.component.CustomObjectMapper;
import com.priyajit.ecommerce.orchestratorservice.component.SecurityContextHelper;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RestController
public class CartServiceController implements CartServiceApi {

    private com.priyajit.ecommerce.cs.api.CartControllerV1Api cartControllerV1Api;
    private CustomObjectMapper objectMapper;
    private SecurityContextHelper securityContextHelper;

    public CartServiceController(
            CartControllerV1Api cartControllerV1Api,
            CustomObjectMapper objectMapper,
            SecurityContextHelper securityContextHelper
    ) {
        this.cartControllerV1Api = cartControllerV1Api;
        this.objectMapper = objectMapper;
        this.securityContextHelper = securityContextHelper;
    }

    /**
     * POST /cart-service/v1/cart
     *
     * @param createCartDto (required)
     * @return OK (status code 200)
     */
    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.CartModel>> createCart(
            @Valid @RequestBody Mono<com.priyajit.ecommerce.fs.model.CreateCartDto> createCartDto,
            final ServerWebExchange exchange
    ) throws Exception {
        return null;
    }


    /**
     * GET /cart-service/v1/cart
     *
     * @param userId (required)
     * @return OK (status code 200)
     */
    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.CartModel>> findCart(
            String authorization,
            @Valid @RequestParam(value = "userId", required = true) String userId,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) throws Exception {
        return Mono.empty()
                .doFirst(() -> log.info("[findCart] reqId: {}", exchange.getRequest().getId()))
                .then(cartControllerV1Api.findCartWithHttpInfo(userId))
                .doOnSuccess((model) -> log.info("After calling cartControllerV1Api.findCartWithHttpInfo"))
                .doOnError((e) -> log.info("After calling cartControllerV1Api.findCartWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .body(objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.CartModel.class)));
    }


    /**
     * GET /cart-service/v1/cart/v2
     *
     * @param userId   (required)
     * @param currency (optional, public to INR)
     * @return OK (status code 200)
     */
    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.CartWithValueModel>> findCartWithValue(
            String authorization,
            String userId,
            Optional<String> currency,
            final ServerWebExchange exchange
    ) throws Exception {
        return Mono.empty()
                .doFirst(() -> log.info("[findCartWithValue] reqId: {}", exchange.getRequest().getId()))
                .then(cartControllerV1Api.findCartWithValueWithHttpInfo(userId, currency.orElse(null)))
                .doOnSuccess((model) -> log.info("After calling cartControllerV1Api.findCartWithValueWithHttpInfo"))
                .doOnError((e) -> log.info("After calling cartControllerV1Api.findCartWithValueWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .body(objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.CartWithValueModel.class)));
    }


    /**
     * POST /cart-service/v1/cart/update-cart-product-quantity
     *
     * @param updateCartProductQuantityDto (required)
     * @return OK (status code 200)
     */
    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.CartModel>> updateCartProductQuantity(
            @Valid Mono<com.priyajit.ecommerce.fs.model.UpdateCartProductQuantityDto> updateCartProductQuantityDto,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) throws Exception {
        return Mono.zip(securityContextHelper.getUserId(), updateCartProductQuantityDto)
                .doFirst(() -> log.info("[updateCartProductQuantity] reqId: {}", exchange.getRequest().getId()))
                .flatMap(tuple -> {
                    var payLoad = objectMapper.map(tuple.getT2(), com.priyajit.ecommerce.cs.model.UpdateCartProductQuantityDto.class);
                    return cartControllerV1Api.updateCartProductQuantityWithHttpInfo(tuple.getT1(), payLoad);
                })
                .doOnSuccess((model) -> log.info("After calling cartControllerV1Api.updateCartProductQuantityWithHttpInfo"))
                .doOnError((e) -> log.info("After calling cartControllerV1Api.updateCartProductQuantityWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .body(objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.CartModel.class)));
    }
}
