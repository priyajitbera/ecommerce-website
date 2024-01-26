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

@Slf4j
public class AwsSqsEmailClientV1 implements EmailClient {


    private AmazonSQS amazonSQS;

    private String awsSqsQueueUrl;

    private final static ObjectWriter jsonWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public AwsSqsEmailClientV1(
            AmazonSQS amazonSQS,
            String awsSqsQueueUrl
    ) {
        this.amazonSQS = amazonSQS;
        this.awsSqsQueueUrl = awsSqsQueueUrl;
    }

    @Override
    public EmailClientStatus send(
            SendEmailDto dto
    ) {

        try {
            String message = jsonWriter.writeValueAsString(dto);
            SendMessageRequest messageRequest = new SendMessageRequest()
                    .withQueueUrl(awsSqsQueueUrl)
                    .withMessageBody(message);

            SendMessageResult messageResult = amazonSQS.sendMessage(messageRequest);
            log.info("Successfully send message:{} to queue:{}",
                    messageResult.getMessageId(), awsSqsQueueUrl);

            return EmailClientStatus.SUCCESS;

        } catch (Exception e) {
            log.error("Failed to send message to queue:{} | {}", awsSqsQueueUrl, e.getMessage());
            e.printStackTrace();

            return EmailClientStatus.FAILURE;
        }
    }
}
