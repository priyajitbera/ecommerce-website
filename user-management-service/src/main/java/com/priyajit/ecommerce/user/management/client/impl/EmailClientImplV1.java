package com.priyajit.ecommerce.user.management.client.impl;

import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@Primary
public class EmailClientImplV1 implements EmailClient {

    private KafkaTemplate<String, Serializable> kafkaTemplate;

    @Value("${kafka.topic}")
    private String KAFKA_TOPIC;

    public EmailClientImplV1(KafkaTemplate<String, Serializable> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public EmailClientStatus send(
            SendEmailDto dto
    ) {
        CompletableFuture<SendResult<String, Serializable>> future = kafkaTemplate.send(
                KAFKA_TOPIC, dto
        );
        try {
            SendResult<String, Serializable> result = future.get();
            log.info("Successfully produced to kafka topic:{}", result.getRecordMetadata().topic());

            return EmailClientStatus.SUCCESS;

        } catch (Exception e) {
            log.error("Error producing to kafka topic:{} | {}", KAFKA_TOPIC, e.getMessage());
            e.printStackTrace();
            return EmailClientStatus.FAILURE;
        }
    }
}
