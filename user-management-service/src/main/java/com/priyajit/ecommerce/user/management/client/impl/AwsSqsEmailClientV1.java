package com.priyajit.ecommerce.user.management.client.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component("awsSqsEmailClientV1")
public class AwsSqsEmailClientV1 implements EmailClient {

    private KafkaTemplate<String, Serializable> kafkaTemplate;

    private AmazonSQS amazonSQS;

    @Value("${aws.sqs.queue-url}")
    private String AWS_SQS_QUEUE_URL;

    private final static ObjectWriter jsonWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public AwsSqsEmailClientV1(
            KafkaTemplate<String, Serializable> kafkaTemplate,
            AmazonSQS amazonSQS
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.amazonSQS = amazonSQS;
    }

    @Override
    public EmailClientStatus send(
            SendEmailDto dto
    ) {

        try {
            String message = jsonWriter.writeValueAsString(dto);
            SendMessageRequest messageRequest = new SendMessageRequest()
                    .withQueueUrl(AWS_SQS_QUEUE_URL)
                    .withMessageBody(message);

            SendMessageResult messageResult = amazonSQS.sendMessage(messageRequest);
            log.info("Successfully send message:{} to queue:{}",
                    messageResult.getMessageId(), AWS_SQS_QUEUE_URL);

            return EmailClientStatus.SUCCESS;

        } catch (Exception e) {
            log.error("Failed to send message to queue:{} | {}", AWS_SQS_QUEUE_URL, e.getMessage());
            e.printStackTrace();

            return EmailClientStatus.FAILURE;
        }
    }
}
