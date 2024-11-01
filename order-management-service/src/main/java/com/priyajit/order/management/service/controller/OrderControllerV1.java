package com.priyajit.order.management.service.controller;

import com.priyajit.order.management.service.dto.CreateOrderDto;
import com.priyajit.order.management.service.dto.PostDeliveryUpdateDto;
import com.priyajit.order.management.service.dto.UpdateDeliveryStatusDto;
import com.priyajit.order.management.service.model.OrderModel;
import com.priyajit.order.management.service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/order")
public class OrderControllerV1 {

    private OrderService orderService;

    public OrderControllerV1(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderModel> createOrder(
            @RequestHeader("userId") String userId,
            @RequestBody CreateOrderDto dto
    ) {
        return ok(orderService.createOrder(dto, userId));
    }

    @GetMapping("/find-one")
    public ResponseEntity<OrderModel> findOrder(
            @RequestParam String orderId
    ) {
        return ok(orderService.findOrder(orderId));
    }

    @GetMapping("/user-orders")
    private ResponseEntity<Page<OrderModel>> findUserOrders(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        return ok(orderService.findUserOrders(userId, page, pageSize));
    }

    @PatchMapping("/update-delivery-status")
    public ResponseEntity<OrderModel> updateDeliveryStatus(
            @RequestBody UpdateDeliveryStatusDto updateDeliveryStatusDto
    ) {
        return ok(orderService.updateDeliveryStatus(updateDeliveryStatusDto));
    }

    @PostMapping("/post-delivery-update")
    public ResponseEntity<OrderModel> postDeliveryUpdate(
            @RequestBody PostDeliveryUpdateDto dto
    ) {
        return ok(orderService.postDeliveryUpdate(dto));
    }
}
