package com.priyajit.order.management.service.service.impl;

import com.priyajit.order.management.service.client.ProductCatalogServiceClient;
import com.priyajit.order.management.service.client.UserManagementServiceClient;
import com.priyajit.order.management.service.domain.PaymentMode;
import com.priyajit.order.management.service.dto.CreateOrderDto;
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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImplV1 implements OrderService {

    private OrderRepository orderRepository;
    private ProductCatalogServiceClient productCatalogServiceClient;
    private UserManagementServiceClient userManagementServiceClient;

    public OrderServiceImplV1(OrderRepository orderRepository, ProductCatalogServiceClient productCatalogServiceClient, UserManagementServiceClient userManagementServiceClient) {
        this.orderRepository = orderRepository;
        this.productCatalogServiceClient = productCatalogServiceClient;
        this.userManagementServiceClient = userManagementServiceClient;
    }

    @Override
    public OrderModel createOrder(CreateOrderDto dto) {
        if (dto == null)
            throw new NullArgumentException("Expected non null value for arg dto:CreateOrderDto");

        // create Order object from dto
        Order order = createOrderObjectFromDto(dto);
        order.setCreatedOn(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());
        order.setLastModifiedOn(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());

        orderRepository.save(order);

        return createOrderModel(order);
    }

    @Override
    public OrderModel findOrder(String orderId) {
        if (orderId == null)
            throw new NullArgumentException("orderId", String.class);

        var order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException.supplier(orderId));

        return createOrderModel(order);
    }

    @Override
    public Page<OrderModel> findUserOrders(String userId, int pageNumber, int pageSize) {
        if (pageNumber < 0) throw new InvalidPageNumberException("pageNumber can not be negative");
        if (pageSize <= 0) throw new InvalidPageSizeException("pageSize can be zero or negative");

        var pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.by("createdOn")));
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageRequest);

        var orderModels = ordersPage.getContent().stream()
                .map(this::createOrderModel)
                .collect(Collectors.toList());

        return new PageImpl<>(
                orderModels,
                pageRequest,
                ordersPage.getTotalPages()
        );
    }

    private Order createOrderObjectFromDto(CreateOrderDto dto) {

        // validate userId
        var user = userManagementServiceClient.findUserByUserId(dto.getUserId())
                .orElseThrow(UserNotFoundException.supplier(dto.getUserId()));

        var orderItems = createOrderItems(dto.getOrderItems());
        var deliveryAddress = createDeliverAddressFromDto(dto.getDeliveryAddress());

        var paymentInfo = createPaymentInfoFromDto(dto.getPaymentInfo());

        System.out.println(paymentInfo.getPaymentMode());

        return Order.builder()
                .userId(dto.getUserId())
                .orderItems(orderItems)
                .orderTotal(dto.getOrderTotal())
                .deliveryAddress(deliveryAddress)
                .paymentInfo(paymentInfo)
                .build();
    }

    private PaymentInfo createPaymentInfoFromDto(CreateOrderDto.PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            throw new NullArgumentException("paymentInfo", CreateOrderDto.PaymentInfo.class);
        }
        if (paymentInfo.getPaymentMode() == null) throw new IncompletePaymentInfoException("PaymentMode is required");

        var paymentInfoBuilder = PaymentInfo.builder()
                .paymentMode(paymentInfo.getPaymentMode());

        // Credit or Debit Card
        if (PaymentMode.CARD == paymentInfo.getPaymentMode()) {
            // validate CardInfo is given
            if (paymentInfo.getCardInfo() == null) {
                throw new IncompletePaymentInfoException(String.format("cardInfo: %s is required for paymentMode:%s",
                        CreateOrderDto.CardInfo.class.getSimpleName(), paymentInfo.getPaymentMode()));
            }
            var cardInfo = CardInfo.builder()
                    .bankName(paymentInfo.getCardInfo().getBankName())
                    .cardHolderName(paymentInfo.getCardInfo().getCardHolderName())
                    .cardNumber(paymentInfo.getCardInfo().getCardNumber())
                    .cardType(paymentInfo.getCardInfo().getCardType())
                    .build();
            paymentInfoBuilder.cardInfo(cardInfo);

        }
        // UPI
        else if (PaymentMode.UPI == paymentInfo.getPaymentMode()) {
            // validate UpiInfo is given
            if (paymentInfo.getUpiInfo() == null)
                throw new IncompletePaymentInfoException(String.format("upiInfo:%s is required for paymentMode: %s",
                        CreateOrderDto.UpiInfo.class.getSimpleName(), paymentInfo.getPaymentMode()));

            var upiInfo = UpiInfo.builder()
                    .upiId(paymentInfo.getUpiInfo().getUpiId())
                    .build();

            paymentInfoBuilder.upiInfo(upiInfo);
        } else if (PaymentMode.CASH_ON_DELIVERY == paymentInfo.getPaymentMode()) {
            // empty block: nothing required
        } else {
            throw new NotImplementedException(String.format("paymentMode: %s is not supported", paymentInfo.getPaymentMode()));
        }

        return paymentInfoBuilder.build();
    }

    private List<OrderItem> createOrderItems(List<CreateOrderDto.OrderItemDto> orderItems) {
        if (orderItems == null) throw new NullArgumentException("orderItems", "List<CreateOrderDto.OrderItem>");

        return orderItems.stream().map(this::createOrderItemFromDto).collect(Collectors.toList());
    }

    private OrderItem createOrderItemFromDto(CreateOrderDto.OrderItemDto dto) {
        if (dto == null) throw new NullArgumentException("dto", CreateOrderDto.OrderItemDto.class);

        return OrderItem.builder()
                .quantity(dto.getQuantity())
                .productId(dto.getProductId())
                .orderItemValue(dto.getOrderItemValue())
                .productUnitPrice(dto.getProductUnitPrice())
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

    private OrderModel createOrderModel(Order order) {
        if (order == null) throw new NullArgumentException("order", Order.class);

        var deliveryAddress =
                order.getDeliveryAddress() == null ? null :
                        createDeliveryAddressModel(order.getDeliveryAddress());

        var orderItems = order.getOrderItems() == null ? null :
                createOrderItemModels(order.getOrderItems());

        var payementInfo = order.getPaymentInfo() == null ? null :
                createPaymentInfoModel(order.getPaymentInfo());

        return OrderModel.builder()
                .orderId(order.getId())
                .createdOn(order.getCreatedOn().atZone(ZoneId.of("UTC")))
                .userId(order.getUserId())
                .orderItems(orderItems)
                .orderTotal(order.getOrderTotal())
                .deliveryAddress(deliveryAddress)
                .paymentInfo(payementInfo)
                .build();
    }

    private OrderModel.PaymentInfoModel createPaymentInfoModel(PaymentInfo paymentInfo) {
        if (paymentInfo == null) throw new NullArgumentException("paymentInfo", PaymentInfo.class);

        var cardInfo = paymentInfo.getCardInfo() == null ? null :
                createCardInfoModel(paymentInfo.getCardInfo());

        var upiInfo = paymentInfo.getUpiInfo() == null ? null :
                createUpiInfoModel(paymentInfo.getUpiInfo());

        return OrderModel.PaymentInfoModel.builder()
                .paymentStatus(paymentInfo.getPaymentStatus())
                .paymentMode(paymentInfo.getPaymentMode())
                .cardInfo(cardInfo)
                .upiInfo(upiInfo)
                .build();
    }

    private OrderModel.UpiInfoModel createUpiInfoModel(UpiInfo upiInfo) {

        return OrderModel.UpiInfoModel.builder()
                .upiId(upiInfo.getUpiId())
                .build();
    }

    private OrderModel.CardInfoModel createCardInfoModel(CardInfo cardInfo) {

        return OrderModel.CardInfoModel.builder()
                .bankName(cardInfo.getBankName())
                .cardHolderName(cardInfo.getCardHolderName())
                .cardType(cardInfo.getCardType())
                .cardNumber(cardInfo.getCardNumber())
                .build();
    }

    private List<OrderModel.OrderItemModel> createOrderItemModels(List<OrderItem> orderItems) {

        return orderItems.stream()
                .map(this::createOrderItemModel)
                .collect(Collectors.toList());
    }

    private OrderModel.OrderItemModel createOrderItemModel(OrderItem orderItem) {

        var product = productCatalogServiceClient.findProductByProductId(orderItem.getProductId())
                .orElseThrow(ProductNotFoundException.supplier(orderItem.getProductId()));

        return OrderModel.OrderItemModel.builder()
                .productId(orderItem.getProductId())
                .productTitle(product.getTitle())
                .quantity(orderItem.getQuantity())
                .productUnitPrice(orderItem.getProductUnitPrice())
                .orderItemValue(orderItem.getOrderItemValue())
                .build();
    }

    private OrderModel.DeliveryAddressModel createDeliveryAddressModel(DeliveryAddress deliveryAddress) {

        return OrderModel.DeliveryAddressModel.builder()
                .buildingNumber(deliveryAddress.getBuildingNumber())
                .houseNumber(deliveryAddress.getHouseNumber())
                .addressLine1(deliveryAddress.getAddressLine1())
                .addressLine2(deliveryAddress.getAddressLine2())
                .landMark(deliveryAddress.getLandMark())
                .contactNumber1(deliveryAddress.getContactNumber1())
                .contactNumber2(deliveryAddress.getContactNumber2())
                .city(deliveryAddress.getCity())
                .pincode(deliveryAddress.getPincode())
                .build();
    }
}
