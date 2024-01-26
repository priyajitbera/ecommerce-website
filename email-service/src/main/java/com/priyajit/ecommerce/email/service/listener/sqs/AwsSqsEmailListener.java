package com.priyajit.ecommerce.email.service.listener.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyajit.ecommerce.email.service.dto.SendEmailDto;
import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.email.service.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AwsSqsEmailListener {

    private String awsSqsQueueUrl;

    private AmazonSQS amazonSQS;

    private EmailSenderService emailSenderService;

    public AwsSqsEmailListener(
            @Nullable AmazonSQS amazonSQS,
            EmailSenderService emailSenderService,
            DbEnvironmentConfiguration dbEnvConfig
    ) {
        this.amazonSQS = amazonSQS;
        this.emailSenderService = emailSenderService;
        this.awsSqsQueueUrl = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_QUEUE_URL);
    }

    /**
     * Method to check new email requests in SQS queue and send using email service
     */
    @Async
    @Scheduled(fixedDelay = 10000) // polling at every 10 second
    public void emailListener() {
        if (amazonSQS == null) return;

        log.info("Checking for new message on SQS Queue:{}", awsSqsQueueUrl);


        // poll queue to check new messages
        ReceiveMessageResult messageResult = amazonSQS.receiveMessage(awsSqsQueueUrl);
        List<Message> messages = messageResult.getMessages();
        log.info("Received {} messages from SQS queue:{}", messages.size(), awsSqsQueueUrl);
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
        deleteMessages(amazonSQS, messages);
    }

    /**
     * Helper method to delete already read messages from SQS queue
     *
     * @param amazonSQS
     * @param messages
     */
    private void deleteMessages(AmazonSQS amazonSQS, List<Message> messages) {

        List<DeleteMessageBatchRequestEntry> deleteEnttries = messages.stream()
                .map(message -> new DeleteMessageBatchRequestEntry()
                        .withReceiptHandle(message.getReceiptHandle())
                        .withId(message.getMessageId())
                )
                .collect(Collectors.toList());

        DeleteMessageBatchRequest deleteMessageBatchRequest = new DeleteMessageBatchRequest()
                .withQueueUrl(awsSqsQueueUrl)
                .withEntries(deleteEnttries);

        DeleteMessageBatchResult batchDeleteResult = amazonSQS.deleteMessageBatch(deleteMessageBatchRequest);

        // log delete results
        batchDeleteResult.getSuccessful().forEach(success -> log.error("Successfully delete message with id:{}",
                success.getId()));
        batchDeleteResult.getFailed().forEach(failed -> log.error("Failed to delete message with id:{} | {}",
                failed.getId(), failed.getMessage()));
    }
}
