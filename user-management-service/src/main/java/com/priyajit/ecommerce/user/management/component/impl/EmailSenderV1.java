package com.priyajit.ecommerce.user.management.component.impl;

import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.component.EmailSender;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderV1 implements EmailSender {

    private EmailClient emailClient;

    private EmailClient awsSqsEmailClient;

    @Value("${email-sending-strategy}")
    private String EMAIL_SENDING_STRATEGY;

    public EmailSenderV1(EmailClient emailClient, EmailClient awsSqsEmailClient) {
        this.emailClient = emailClient;
        this.awsSqsEmailClient = awsSqsEmailClient;
    }

    @Override
    public EmailClientStatus send(SendEmailDto dto) {

        // send email via configured strategy
        if ("SQS".equalsIgnoreCase(EMAIL_SENDING_STRATEGY)) {

            return awsSqsEmailClient.send(dto);

        } else if ("KAFKA".equalsIgnoreCase(EMAIL_SENDING_STRATEGY)) {

            return emailClient.send(dto);

        } else {
            throw new RuntimeException("Email sending strategy not correct!");
        }
    }
}
