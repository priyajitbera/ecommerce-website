package com.priyajit.ecommerce.payment.service.service;

import com.priyajit.ecommerce.payment.service.dto.ConfirmPaymentStatusDto;
import com.priyajit.ecommerce.payment.service.dto.CreatePaymentDto;
import com.priyajit.ecommerce.payment.service.model.PaymentModel;

public interface PaymentService {

    PaymentModel createPayment(CreatePaymentDto dto);

    PaymentModel confirmPaymentStatus(ConfirmPaymentStatusDto dto);

    PaymentModel findPayment(String paymentId);
}
