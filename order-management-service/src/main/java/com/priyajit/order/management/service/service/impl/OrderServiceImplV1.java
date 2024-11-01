package com.priyajit.order.management.service.service.impl;

import com.priyajit.order.management.service.client.PaymentServiceClient;
import com.priyajit.order.management.service.client.ProductCatalogServiceClient;
import com.priyajit.order.management.service.client.UserManagementServiceClient;
import com.priyajit.order.management.service.client.dto.CreatePaymentDto;
import com.priyajit.order.management.service.client.model.PaymentModel;
import com.priyajit.order.management.service.domain.*;
import com.priyajit.order.management.service.dto.CreateOrderDto;
import com.priyajit.order.management.service.dto.PostDeliveryUpdateDto;
import com.priyajit.order.management.service.dto.UpdateDeliveryStatusDto;
import com.priyajit.order.management.service.event.dto.PaymentStatusConfirmationEventDto;
import com.priyajit.order.management.service.exception.*;
import com.priyajit.order.management.service.model.OrderModel;
import com.priyajit.order.management.service.mongodoc.*;
import com.priyajit.order.management.service.mongorepository.OrderRepository;
import com.priyajit.order.management.service.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImplV1 implements OrderService {

    private OrderRepository orderRepository;
    private ProductCatalogServiceClient productCatalogServiceClient;
    private UserManagementServiceClient userManagementServiceClient;
    private PaymentServiceClient paymentServiceClient;

    public OrderServiceImplV1(OrderRepository orderRepository, ProductCatalogServiceClient productCatalogServiceClient, UserManagementServiceClient userManagementServiceClient, PaymentServiceClient paymentServiceClient) {
        this.orderRepository = orderRepository;
        this.productCatalogServiceClient = productCatalogServiceClient;
        this.userManagementServiceClient = userManagementServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    @Transactional
    public OrderModel createOrder(CreateOrderDto dto, String userId) {
        if (dto == null) throw new NullArgumentException("Expected non null value for arg dto:CreateOrderDto");
        if (userId == null) throw new NullArgumentException("Non null value required for arg userId");

        // create Order object from dto
        Order order = createOrderObjectFromDto(dto);
        order.setUserId(userId);
        order.setCreatedOn(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());
        order.setLastModifiedOn(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());

        // for PaymentMode.CASH_ON_DELIVERY directly confirm order
        if (PaymentMode.CASH_ON_DELIVERY == dto.getPaymentInfo().getPaymentMode()) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
        } else if (PaymentMode.CARD == dto.getPaymentInfo().getPaymentMode() || PaymentMode.UPI == dto.getPaymentInfo().getPaymentMode()) {
            order.setOrderStatus(OrderStatus.NEW);
        }

        // set DeliveryStatus as NEW
        order.getDeliveryInfo().setDeliveryStatus(DeliveryStatus.NEW);

        // save order
        orderRepository.save(order);

        var paymentModel = createPaymentRequest(order.getId(), order.getOrderTotal(), dto.getPaymentInfo());
        order.setPaymentInfo(PaymentInfo.builder()
                .paymentMode(dto.getPaymentInfo().getPaymentMode())
                .paymentId(paymentModel.getId())
                .paymentStatus(paymentModel.getPaymentStatus())
                .build());

        System.out.println(paymentModel.getId());
        // save order object with paymentInfo
        orderRepository.save(order);

        return OrderModel.buildFrom(order);
    }

    private PaymentModel createPaymentRequest(String orderId, BigDecimal amount, CreateOrderDto.PaymentInfo paymentInfo) {

        if (PaymentMode.CARD == paymentInfo.getPaymentMode()) {
            return createCardPaymentRequest(
                    orderId,
                    amount,
                    paymentInfo.getCurrency(),
                    paymentInfo.getCardInfo().getCardType(),
                    paymentInfo.getCardInfo().getCardNumber(),
                    paymentInfo.getCardInfo().getCardHolderName(),
                    paymentInfo.getCardInfo().getBankName()
            );
        } else if (PaymentMode.UPI == paymentInfo.getPaymentMode()) {
            return createUpiPaymentRequest(
                    orderId,
                    amount,
                    paymentInfo.getCurrency(),
                    paymentInfo.getUpiInfo().getUpiId()
            );
        } else if (PaymentMode.CASH_ON_DELIVERY == paymentInfo.getPaymentMode()) {
            return createCashOnDeliveryPayment(
                    orderId,
                    amount,
                    paymentInfo.getCurrency()
            );
        } else {
            throw new NotImplementedException(String.format("Not implemented for PaymentMode: %s",
                    paymentInfo.getPaymentMode()));
        }
    }

    private PaymentModel createCardPaymentRequest(
            String orderId,
            BigDecimal amount,
            String currency,
            CardType cardType,
            String cardNumber,
            String cardHolderName,
            String bankName
    ) {
        return paymentServiceClient.createPayment(
                CreatePaymentDto.builder()
                        .orderId(orderId)
                        .amount(amount)
                        .currency(currency)
                        .paymentMode(PaymentMode.CARD)
                        .cardInfo(CreatePaymentDto.CardInfoDto.builder()
                                .cardType(cardType)
                                .cardNumber(cardNumber)
                                .cardHolderName(cardHolderName)
                                .bankName(bankName)
                                .build())
                        .build()
        );
    }

    private PaymentModel createUpiPaymentRequest(
            String orderId,
            BigDecimal amount,
            String currency,
            String upiId
    ) {
        return paymentServiceClient.createPayment(
                CreatePaymentDto.builder()
                        .orderId(orderId)
                        .amount(amount)
                        .currency(currency)
                        .paymentMode(PaymentMode.UPI)
                        .upiInfo(
                                CreatePaymentDto.UpiInfoDto.builder()
                                        .upiId(upiId)
                                        .build())
                        .build()
        );
    }

    private PaymentModel createCashOnDeliveryPayment(
            String orderId,
            BigDecimal amount,
            String currency
    ) {
        return paymentServiceClient.createPayment(
                CreatePaymentDto.builder()
                        .orderId(orderId)
                        .amount(amount)
                        .currency(currency)
                        .paymentMode(PaymentMode.CASH_ON_DELIVERY)
                        .build()
        );
    }

    @Override
    public OrderModel findOrder(String orderId) {
        if (orderId == null)
            throw new NullArgumentException("orderId", String.class);

        var order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException.supplier(orderId));

        return OrderModel.buildFrom(order);
    }

    @Override
    public Page<OrderModel> findUserOrders(String userId, int pageNumber, int pageSize) {
        if (pageNumber < 0) throw new InvalidPageNumberException("pageNumber can not be negative");
        if (pageSize <= 0) throw new InvalidPageSizeException("pageSize can be zero or negative");

        var pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.by("createdOn")));
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageRequest);

        var orderModels = OrderModel.buildFrom(ordersPage.getContent());

        return new PageImpl<>(
                orderModels,
                pageRequest,
                ordersPage.getTotalPages()
        );
    }

    @Override
    public OrderModel updatePaymentStatus(PaymentStatusConfirmationEventDto dto) {
        var order = orderRepository.findByPaymentInfo_paymentId(dto.getPaymentId())
                .orElseThrow(OrderNotFoundException.supplierByPaymentId(dto.getPaymentId()));

        order.getPaymentInfo().setPaymentStatus(dto.getPaymentStatus());
        order.getPaymentInfo().setPaymentStatusLastUpdatedOn(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());

        // update OrderStatus as CONFIRMED if PaymentStatus is COMPLETED
        if (PaymentStatus.COMPLETED == order.getPaymentInfo().getPaymentStatus()) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
        }

        orderRepository.save(order);

        return OrderModel.buildFrom(order);
    }

    @Override
    @Transactional
    public OrderModel updateDeliveryStatus(UpdateDeliveryStatusDto dto) {
        if (dto == null)
            throw new NullArgumentException("dto", UpdateDeliveryStatusDto.class);

        if (dto.getOrderId() == null)
            throw new NullArgumentException("dto.orderId", String.class);

        var order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(OrderNotFoundException.supplier(dto.getOrderId()));

        // update the DeliveryStatus
        order.getDeliveryInfo().setDeliveryStatus(dto.getDeliveryStatus());

        // save updates
        orderRepository.save(order);

        return OrderModel.buildFrom(order);
    }

    @Override
    public OrderModel postDeliveryUpdate(PostDeliveryUpdateDto dto) {
        if (dto == null)
            throw new NullArgumentException("dto", PostDeliveryUpdateDto.class);

        if (dto.getOrderId() == null)
            throw new NullArgumentException("dto.orderId", String.class);

        var order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(OrderNotFoundException.supplier(dto.getOrderId()));

        if (order.getDeliveryInfo().getDeliveryUpdates() == null)
            order.getDeliveryInfo().setDeliveryUpdates(new ArrayList<>());

        var deliveryUpdate = DeliveryUpdate.builder()
                .timeStamp(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime())
                .message(dto.getMessage())
                .build();

        order.getDeliveryInfo().getDeliveryUpdates().add(deliveryUpdate);

        // save updates
        orderRepository.save(order);

        return OrderModel.buildFrom(order);
    }

    private Order createOrderObjectFromDto(CreateOrderDto dto) {

        var orderItems = createOrderItems(dto.getOrderItems());
        var deliveryInfo = createDeliveryInfoFromDto(dto);

        return Order.builder()
                .orderItems(orderItems)
                .orderTotal(dto.getOrderTotal())
                .deliveryInfo(deliveryInfo)
                .build();
    }

    private List<OrderItem> createOrderItems(List<CreateOrderDto.OrderItemDto> orderItems) {
        if (orderItems == null) throw new NullArgumentException("orderItems", "List<CreateOrderDto.OrderItem>");

        return orderItems.stream().map(this::createOrderItemFromDto).collect(Collectors.toList());
    }

    private OrderItem createOrderItemFromDto(CreateOrderDto.OrderItemDto dto) {
        if (dto == null) throw new NullArgumentException("dto", CreateOrderDto.OrderItemDto.class);

        var product = productCatalogServiceClient.findProductByProductId(dto.getProductId())
                .orElseThrow(ProductNotFoundException.supplier(dto.getProductId()));

        return OrderItem.builder()
                .quantity(dto.getQuantity())
                .productId(dto.getProductId())
                .productTitle(product.getTitle())
                .orderItemValue(dto.getOrderItemValue())
                .productUnitPrice(dto.getProductUnitPrice())
                .build();
    }

    private DeliveryInfo createDeliveryInfoFromDto(CreateOrderDto dto) {
        if (dto == null) throw new NullArgumentException("dto", CreateOrderDto.class);

        var deliverAddress = dto.getDeliveryAddress() == null ? null :
                createDeliverAddressFromDto(dto.getDeliveryAddress());

        return DeliveryInfo.builder()
                .deliveryAddress(deliverAddress)
                .build();
    }

    private DeliveryAddress createDeliverAddressFromDto(CreateOrderDto.DeliveryAddressDto dto) {
        if (dto == null) throw new NullArgumentException("dto", CreateOrderDto.DeliveryAddressDto.class);

        return DeliveryAddress.builder()
                .buildingNumber(dto.getBuildingNumber())
                .houseNumber(dto.getHouseNumber())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .landMark(dto.getLandMark())
                .contactNumber1(dto.getContactNumber1())
                .contactNumber2(dto.getContactNumber2())
                .city(dto.getCity())
                .pincode(dto.getPincode()).build();
    }
}
