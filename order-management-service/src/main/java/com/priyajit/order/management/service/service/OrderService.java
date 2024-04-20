package com.priyajit.order.management.service.service;

import com.priyajit.order.management.service.event.dto.PaymentStatusConfirmationEventDto;
import com.priyajit.order.management.service.dto.CreateOrderDto;
import com.priyajit.order.management.service.model.OrderModel;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderModel createOrder(CreateOrderDto dto);

    OrderModel findOrder(String orderId);

    Page<OrderModel> findUserOrders(String userId, int page, int pageSize);

    OrderModel updatePaymentStatus(PaymentStatusConfirmationEventDto dto);
}
