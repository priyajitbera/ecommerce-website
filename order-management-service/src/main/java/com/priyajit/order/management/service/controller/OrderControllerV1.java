package com.priyajit.order.management.service.controller;

import com.priyajit.order.management.service.dto.CreateOrderDto;
import com.priyajit.order.management.service.dto.PostDeliveryUpdateDto;
import com.priyajit.order.management.service.dto.UpdateDeliveryStatusDto;
import com.priyajit.order.management.service.model.OrderModel;
import com.priyajit.order.management.service.model.Response;
import com.priyajit.order.management.service.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController("/v1/order")
public class OrderControllerV1 {

    private OrderService orderService;

    public OrderControllerV1(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Response<OrderModel>> createOrder(
            @RequestBody CreateOrderDto dto
    ) {
        try {
            var orderModel = orderService.createOrder(dto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Response.<OrderModel>builder()
                            .data(orderModel)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<OrderModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<OrderModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/find-one")
    public OrderModel findOrder(@RequestParam String orderId) {
        return orderService.findOrder(orderId);
    }

    @GetMapping("/user-orders")
    private Page<OrderModel> findUserOrders(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        return orderService.findUserOrders(userId, page, pageSize);
    }

    @PatchMapping("/update-delivery-status")
    public OrderModel updateDeliveryStatus(
            @RequestBody UpdateDeliveryStatusDto updateDeliveryStatusDto
    ) {
        return orderService.updateDeliveryStatus(updateDeliveryStatusDto);
    }

    @PostMapping("/post-delivery-update")
    public OrderModel postDelivateUpdate(
            @RequestBody PostDeliveryUpdateDto dto
    ) {
        return orderService.postDeliveryUpdate(dto);
    }
}
