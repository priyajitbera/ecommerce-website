package com.priyajit.ecommerce.payment.service.client.impl;

import com.priyajit.ecommerce.payment.service.client.PaymentStatusConfirmationEventProducerClient;
import com.priyajit.ecommerce.payment.service.event.dto.PaymentStatusConfirmationEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class PaymentStatusConfirmationEventProducerClientKafka implements PaymentStatusConfirmationEventProducerClient {

    private KafkaTemplate<String, Serializable> kafkaTemplate;
    private String topic;

    public PaymentStatusConfirmationEventProducerClientKafka(
            KafkaTemplate<String, Serializable> kafkaTemplate,
            String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendPaymentStatusConfirmationEvent(PaymentStatusConfirmationEventDto dto) {

        var sendResultFuture = kafkaTemplate.send(topic, dto);
        try {
            var sendResult = sendResultFuture.get();
            log.info("Successfully produced to kafka topic:{}", sendResult.getRecordMetadata().topic());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
