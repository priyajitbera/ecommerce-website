package com.priyajit.ecommerce.user.management.component.impl;

import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.component.EmailSender;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class EmailSenderV1 implements EmailSender {

    private EmailClient kafkaEmailClient;

    private EmailClient awsSqsEmailClient;

    private String EMAIL_SENDING_STRATEGY;

    public EmailSenderV1(
            @Nullable @Qualifier("kafkaEmailClientV1") EmailClient kafkaEmailClient,
            @Nullable @Qualifier("awsSqsEmailClientV1") EmailClient awsSqsEmailClient,
            DbEnvironmentConfiguration dbEnvironmentConfiguration
    ) {
        this.kafkaEmailClient = kafkaEmailClient;
        this.awsSqsEmailClient = awsSqsEmailClient;
        this.EMAIL_SENDING_STRATEGY = dbEnvironmentConfiguration.getProperty(
                DbEnvironmentConfiguration.Keys.EMAIL_SENDING_STRATEGY
        );
    }

    @Override
    public EmailClientStatus send(SendEmailDto dto) {

        // send email via configured strategy
        if (Objects.equals(DbEnvironmentConfiguration.Values.EMAIL_SENDING_STRATEGY_AWS_SQS, EMAIL_SENDING_STRATEGY)) {
            if (awsSqsEmailClient == null) {
                log.error("awsSqsEmailClient is not configured while EMAIL_SENDING_STRATEGY_AWS_SQS is set to {}",
                        EMAIL_SENDING_STRATEGY);
                throw new RuntimeException("Invalid configuration of EMAIL_SENDING_STRATEGY");
            }
            return awsSqsEmailClient.send(dto);

        } else if (Objects.equals(DbEnvironmentConfiguration.Values.EMAIL_SENDING_STRATEGY_KAFKA, EMAIL_SENDING_STRATEGY)) {
            if (kafkaEmailClient == null) {
                log.error("kafkaEmailClient is not configured, while EMAIL_SENDING_STRATEGY is set to {}",
                        EMAIL_SENDING_STRATEGY);
                throw new RuntimeException("Invalid configuration of EMAIL_SENDING_STRATEGY");
            }
            return kafkaEmailClient.send(dto);

        } else { // when EMAIL_SENDING_STRATEGY not one of the defined
            throw new RuntimeException("Invalid configuration of EMAIL_SENDING_STRATEGY");
        }
    }
}
