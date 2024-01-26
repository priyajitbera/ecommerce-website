package com.priyajit.email.service.listener.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyajit.email.service.dto.SendEmailDto;
import com.priyajit.email.service.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AwsSqsEmailListener {

    @Value("${aws.sqs.queue-url}")
    private String AWS_SQS_QUEUE_URL;

    private AmazonSQS sqs;

    private EmailSenderService emailSenderService;

    public AwsSqsEmailListener(AmazonSQS sqs, EmailSenderService emailSenderService) {
        this.sqs = sqs;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Method to check new email requests in SQS queue and send using email service
     */
    @Scheduled(fixedDelay = 10000) // polling at every 10 second
    public void emailListener() {
        log.info("Checking for new message on SQS Queue:{}", AWS_SQS_QUEUE_URL);


        // poll queue to check new messages
        ReceiveMessageResult messageResult = sqs.receiveMessage(AWS_SQS_QUEUE_URL);
        List<Message> messages = messageResult.getMessages();
        log.info("Received {} messages from SQS queue:{}", messages.size(), AWS_SQS_QUEUE_URL);
        if (Objects.equals(messages.size(), 0)) return;


        List<SendEmailDto> emailDtos = messages.stream().map(message -> {
                    log.info("Reading message with id:{}", message.getMessageId());
                    String messageBody = message.getBody();

                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        SendEmailDto emailDto = objectMapper.readValue(messageBody, SendEmailDto.class);
                        return emailDto;

                    } catch (JsonProcessingException e) {
                        log.info("Error while mapping message body to {} messageId:{} | {}",
                                SendEmailDto.class.getName(), message.getMessageId(), e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull) // filter out nulls
                .collect(Collectors.toList());

        if (emailDtos.size() > 0) {
            emailSenderService.sendEmail(emailDtos);
        }

        // delete messages that are read
        deleteMessages(sqs, messages);
    }

    /**
     * Helper method to delete already read messages from SQS queue
     *
     * @param sqs
     * @param messages
     */
    private void deleteMessages(AmazonSQS sqs, List<Message> messages) {

        List<DeleteMessageBatchRequestEntry> deleteEnttries = messages.stream()
                .map(message -> new DeleteMessageBatchRequestEntry()
                        .withReceiptHandle(message.getReceiptHandle())
                        .withId(message.getMessageId())
                )
                .collect(Collectors.toList());

        DeleteMessageBatchRequest deleteMessageBatchRequest = new DeleteMessageBatchRequest()
                .withQueueUrl(AWS_SQS_QUEUE_URL)
                .withEntries(deleteEnttries);

        DeleteMessageBatchResult batchDeleteResult = sqs.deleteMessageBatch(deleteMessageBatchRequest);

        // log delete results
        batchDeleteResult.getSuccessful().forEach(success -> log.error("Successfully delete message with id:{}",
                success.getId()));
        batchDeleteResult.getFailed().forEach(failed -> log.error("Failed to delete message with id:{} | {}",
                failed.getId(), failed.getMessage()));
    }
}
