package com.priyajit.ecommerce.payment.service.client;

import com.priyajit.ecommerce.payment.service.event.dto.PaymentStatusConfirmationEventDto;

public interface PaymentStatusConfirmationEventProducerClient {

    void sendPaymentStatusConfirmationEvent(PaymentStatusConfirmationEventDto dto);
}
