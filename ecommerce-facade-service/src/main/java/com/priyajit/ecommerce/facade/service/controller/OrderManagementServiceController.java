package com.priyajit.ecommerce.facade.service.controller;


import com.priyajit.ecommerce.facade.service.component.CustomObjectMapper;
import com.priyajit.ecommerce.facade.service.component.SecurityContextHelper;
import com.priyajit.ecommerce.fs.model.CreateOrderDto;
import com.priyajit.ecommerce.fs.model.PostDeliveryUpdateDto;
import com.priyajit.ecommerce.oms.api.OrderControllerV1Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RestController
public class OrderManagementServiceController implements com.priyajit.ecommerce.fs.api.OrderManagementServiceApi {

    private final com.priyajit.ecommerce.oms.api.OrderControllerV1Api orderControllerV1Api;
    private final CustomObjectMapper objectMapper;
    private final SecurityContextHelper securityContextHelper;

    public OrderManagementServiceController(OrderControllerV1Api orderControllerV1Api, CustomObjectMapper objectMapper, SecurityContextHelper securityContextHelper) {
        this.orderControllerV1Api = orderControllerV1Api;
        this.objectMapper = objectMapper;
        this.securityContextHelper = securityContextHelper;
    }

    @Override
    @PreAuthorize("hasAuthority('BUYER')")
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.OrderModel>> createOrder(
            Mono<CreateOrderDto> createOrderDto,
            ServerWebExchange exchange
    ) throws Exception {
        return Mono.empty()
                .doFirst(() -> log.info("[createOrder] reqId: {}", exchange.getRequest().getId()))
                .then(
                        Mono.zip(securityContextHelper.getUserId(), createOrderDto)
                                .flatMap(tuple -> {
                                    var userId = tuple.getT1();
                                    var payload = objectMapper.map(tuple.getT2(), com.priyajit.ecommerce.oms.model.CreateOrderDto.class);
                                    return orderControllerV1Api.createOrderWithHttpInfo(userId, payload);
                                }))
                .doOnSuccess((model) -> log.info("[createOrder] After calling orderControllerV1Api.createOrderWithHttpInfo"))
                .doOnError((e) -> log.error("[createOrder] After calling orderControllerV1Api.createOrderWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(
                        objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.OrderModel.class)));
    }

    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.OrderModel>> findOrder(
            String orderId,
            ServerWebExchange exchange
    ) throws Exception {
        return Mono.empty()
                .doFirst(() -> log.info("[findOrder] reqId: {}", exchange.getRequest().getId()))
                .then(orderControllerV1Api.findOrderWithHttpInfo(orderId))
                .doOnSuccess((model) -> log.info("[findOrder] After calling orderControllerV1Api.findOrderWithHttpInfo"))
                .doOnError((e) -> log.error("[findOrder] After calling orderControllerV1Api.findOrderWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(
                        objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.OrderModel.class)));
    }

    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.PageOrderModel>> findUserOrders(
            Optional<Integer> page,
            Optional<Integer> pageSize,
            ServerWebExchange exchange
    ) throws Exception {
        return Mono.empty()
                .doFirst(() -> log.info("[findUserOrders] reqId: {}", exchange.getRequest().getId()))
                .then(securityContextHelper.getUserId())
                .flatMap(userId -> orderControllerV1Api.findUserOrdersWithHttpInfo(userId, page.orElse(null), pageSize.orElse(null)))
                .doOnSuccess((model) -> log.info("[findUserOrders] After calling orderControllerV1Api.findUserOrdersWithHttpInfo"))
                .doOnError((e) -> log.error("[findUserOrders] After calling orderControllerV1Api.findUserOrdersWithHttpInfo error occurred, {}", e.getMessage()))
                .doOnSuccess((model) -> log.info("[findUserOrders] mapping response to required response model"))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(
                        objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.PageOrderModel.class)));
    }

    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.OrderModel>> postDeliveryUpdate(
            Mono<PostDeliveryUpdateDto> postDeliveryUpdateDto,
            ServerWebExchange exchange
    ) throws Exception {
        return postDeliveryUpdateDto
                .doFirst(() -> log.info("[postDeliveryUpdate] reqId: {}", exchange.getRequest().getId()))
                .flatMap(dto -> {
                    var payload = objectMapper.map(dto, com.priyajit.ecommerce.oms.model.PostDeliveryUpdateDto.class);
                    return orderControllerV1Api.postDeliveryUpdateWithHttpInfo(payload);
                })
                .doOnSuccess((model) -> log.info("[postDeliveryUpdate] After calling orderControllerV1Api.findUserOrdersWithHttpInfo"))
                .doOnError((e) -> log.error("[postDeliveryUpdate] After calling orderControllerV1Api.findUserOrdersWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(
                        objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.OrderModel.class)));
    }

    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.OrderModel>> updateDeliveryStatus(
            Mono<com.priyajit.ecommerce.fs.model.UpdateDeliveryStatusDto> updateDeliveryStatusDto,
            ServerWebExchange exchange
    ) throws Exception {
        return updateDeliveryStatusDto
                .doFirst(() -> log.info(""))
                .flatMap(dto -> {
                    var payload = objectMapper.map(dto, com.priyajit.ecommerce.oms.model.UpdateDeliveryStatusDto.class);
                    return orderControllerV1Api.updateDeliveryStatusWithHttpInfo(payload);
                })
                .doOnSuccess((model) -> log.info("[findUserOrders] After calling orderControllerV1Api.findUserOrdersWithHttpInfo"))
                .doOnError((e) -> log.error("[findUserOrders] After calling orderControllerV1Api.findUserOrdersWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(
                        objectMapper.map(response.getBody(), com.priyajit.ecommerce.fs.model.OrderModel.class)));
    }
}