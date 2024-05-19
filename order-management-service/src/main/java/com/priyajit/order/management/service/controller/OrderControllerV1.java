package com.priyajit.order.management.service.controller;

import com.priyajit.order.management.service.dto.CreateOrderDto;
import com.priyajit.order.management.service.dto.PostDeliveryUpdateDto;
import com.priyajit.order.management.service.dto.UpdateDeliveryStatusDto;
import com.priyajit.order.management.service.model.OrderModel;
import com.priyajit.order.management.service.model.Response;
import com.priyajit.order.management.service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.priyajit.order.management.service.controller.ControllerHelper.supplyResponse;

@Slf4j
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
        return supplyResponse(() -> orderService.createOrder(dto), log);
    }

    @GetMapping("/find-one")
    public ResponseEntity<Response<OrderModel>> findOrder(
            @RequestParam String orderId
    ) {
        return supplyResponse(() -> orderService.findOrder(orderId), log);
    }

    @GetMapping("/user-orders")
    private ResponseEntity<Response<Page<OrderModel>>> findUserOrders(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        return supplyResponse(() -> orderService.findUserOrders(userId, page, pageSize), log);
    }

    @PatchMapping("/update-delivery-status")
    public ResponseEntity<Response<OrderModel>> updateDeliveryStatus(
            @RequestBody UpdateDeliveryStatusDto updateDeliveryStatusDto
    ) {
        return supplyResponse(() -> orderService.updateDeliveryStatus(updateDeliveryStatusDto), log);
    }

    @PostMapping("/post-delivery-update")
    public ResponseEntity<Response<OrderModel>> postDelivateUpdate(
            @RequestBody PostDeliveryUpdateDto dto
    ) {
        return supplyResponse(() -> orderService.postDeliveryUpdate(dto), log);
    }
}
