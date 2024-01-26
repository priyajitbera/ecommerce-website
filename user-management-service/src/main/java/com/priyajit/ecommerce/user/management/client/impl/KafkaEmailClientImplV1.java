package com.priyajit.ecommerce.user.management.client.impl;

import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class KafkaEmailClientImplV1 implements EmailClient {

    private KafkaTemplate<String, Serializable> kafkaTemplate;

    private String kafkaTopic;

    public KafkaEmailClientImplV1(
            KafkaTemplate<String, Serializable> kafkaTemplate,
            String kafkaTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    public EmailClientStatus send(
            SendEmailDto dto
    ) {
        CompletableFuture<SendResult<String, Serializable>> future = kafkaTemplate.send(
                kafkaTopic, dto
        );
        try {
            SendResult<String, Serializable> result = future.get();
            log.info("Successfully produced to kafka topic:{}", result.getRecordMetadata().topic());

            return EmailClientStatus.SUCCESS;

        } catch (Exception e) {
            log.error("Error producing to kafka topic:{} | {}", kafkaTopic, e.getMessage());
            e.printStackTrace();
            return EmailClientStatus.FAILURE;
        }
    }
}
