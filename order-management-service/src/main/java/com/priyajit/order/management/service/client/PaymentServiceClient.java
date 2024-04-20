package com.priyajit.order.management.service.client;

import com.priyajit.order.management.service.client.dto.CreatePaymentDto;
import com.priyajit.order.management.service.client.model.PaymentModel;

public interface PaymentServiceClient {

    PaymentModel createPayment(CreatePaymentDto dto);

}
