package com.priyajit.ecommerce.payment.service.service.impl;

import com.priyajit.ecommerce.payment.service.client.PaymentStatusConfirmationEventProducerClient;
import com.priyajit.ecommerce.payment.service.domain.PaymentMode;
import com.priyajit.ecommerce.payment.service.domain.PaymentStatus;
import com.priyajit.ecommerce.payment.service.dto.ConfirmPaymentStatusDto;
import com.priyajit.ecommerce.payment.service.dto.CreatePaymentDto;
import com.priyajit.ecommerce.payment.service.entity.CardInfo;
import com.priyajit.ecommerce.payment.service.entity.Payment;
import com.priyajit.ecommerce.payment.service.entity.UpiInfo;
import com.priyajit.ecommerce.payment.service.event.dto.PaymentStatusConfirmationEventDto;
import com.priyajit.ecommerce.payment.service.exception.*;
import com.priyajit.ecommerce.payment.service.model.PaymentModel;
import com.priyajit.ecommerce.payment.service.repository.PaymentRepository;
import com.priyajit.ecommerce.payment.service.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Slf4j
@Service
public class PaymentServiceImplV1 implements PaymentService {

    private PaymentRepository paymentRepository;
    private PaymentStatusConfirmationEventProducerClient paymentStatusConfirmationEventProducerClient;

    public PaymentServiceImplV1(
            PaymentRepository paymentRepository,
            @Qualifier("paymentStatusConfirmationEventProducerClientKafka")
            PaymentStatusConfirmationEventProducerClient paymentStatusConfirmationEventProducerClient) {
        this.paymentRepository = paymentRepository;
        this.paymentStatusConfirmationEventProducerClient = paymentStatusConfirmationEventProducerClient;
    }

    @Override
    public PaymentModel createPayment(CreatePaymentDto dto) {

        var payment = createPaymentObjectFromDto(dto);
        paymentRepository.saveAndFlush(payment);

        return PaymentModel.buildFrom(payment);
    }

    @Override
    public PaymentModel confirmPaymentStatus(ConfirmPaymentStatusDto dto) {

        if (dto == null) throw new NullArgumentException("dto", ConfirmPaymentStatusDto.class);

        if (dto.getPaymentId() == null) throw new NullArgumentException("paymentId", String.class);

        if (dto.getPaymentStatus() == PaymentStatus.REQUESTED) {
            throw new InvalidPaymentIStatusException(String.format("Expected payment status %s or %s",
                    PaymentStatus.COMPLETED, PaymentStatus.FAILED));
        }

        var payment = paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(PaymentNotFoundException.supplier(dto.getPaymentId()));

        if (PaymentStatus.COMPLETED == payment.getPaymentStatus() || PaymentStatus.FAILED == payment.getPaymentStatus()) {
            throw new InvalidPaymentStatusConfirmationRequestException(String.format("PaymentStatus is already confirmed for paymentId: %s",
                    payment.getId()));
        }

        // update payment status
        payment.setPaymentStatus(dto.getPaymentStatus());
        payment.setFailureReason(dto.getFailureReason());

        // save changes
        paymentRepository.saveAndFlush(payment);

        // publish the payment status confirmation event
        try {
            paymentStatusConfirmationEventProducerClient.sendPaymentStatusConfirmationEvent(
                    PaymentStatusConfirmationEventDto.builder()
                            .paymentId(payment.getId())
                            .paymentStatus(payment.getPaymentStatus())
                            .build()
            );
            payment.setPaymentStatusConfirmationEventPublished(true);
            payment.setPaymentStatusConfirmationEventPublishedOn(ZonedDateTime.now());
            paymentRepository.saveAndFlush(payment);
            log.info("Successfully PaymentStatusConfirmationEvent");

        } catch (Exception e) {
            log.error("Failed to publish PaymentStatusConfirmationEvent for paymentId: {}, {}",
                    payment.getId(), e.getMessage());
            e.printStackTrace();
        }
        return PaymentModel.buildFrom(payment);
    }

    @Override
    public PaymentModel findPayment(String paymentId) {
        if (paymentId == null) throw new NullArgumentException("paymentId", String.class);

        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException.supplier(paymentId));

        return PaymentModel.buildFrom(payment);
    }

    private Payment createPaymentObjectFromDto(CreatePaymentDto dto) {
        if (dto == null) throw new NullArgumentException("dto", CreatePaymentDto.class);

        if (dto.getPaymentMode() == null)
            throw new IncompletePaymentInfoException("PaymentMode information required");

        var paymentBuilder = Payment.builder()
                .paymentMode(dto.getPaymentMode())
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .paymentStatus(PaymentStatus.REQUESTED);

        if (PaymentMode.CARD == dto.getPaymentMode()) {
            if (dto.getCardInfo() == null)
                throw new IncompletePaymentInfoException(String.format("CardInfo is required for paymentMode: %s",
                        dto.getPaymentMode()));

            var cardInfo = createCardInfoObjectFromDto(dto.getCardInfo());
            paymentBuilder.cardInfo(cardInfo);
        }

        if (PaymentMode.UPI == dto.getPaymentMode()) {
            if (dto.getUpiInfo() == null)
                throw new IncompletePaymentInfoException(String.format("UpiInfo is required for paymentMode: %s",
                        dto.getPaymentMode()));

            var upiInfo = createUpiInfoObjectFromDto(dto.getUpiInfo());
            paymentBuilder.upiInfo(upiInfo);
        }

        return paymentBuilder.build();
    }

    private CardInfo createCardInfoObjectFromDto(CreatePaymentDto.CardInfoDto cardInfo) {

        return CardInfo.builder()
                .cardType(cardInfo.getCardType())
                .cardHolderName(cardInfo.getCardHolderName())
                .bankName(cardInfo.getBankName())
                .cardNumber(cardInfo.getCardNumber())
                .build();
    }

    private UpiInfo createUpiInfoObjectFromDto(CreatePaymentDto.UpiInfoDto upiInfo) {

        return UpiInfo.builder()
                .upiId(upiInfo.getUpiId())
                .build();
    }
}
