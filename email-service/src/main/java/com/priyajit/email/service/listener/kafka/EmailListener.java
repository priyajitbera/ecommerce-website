package com.priyajit.email.service.listener.kafka;

import com.priyajit.email.service.dto.SendEmailDto;
import com.priyajit.email.service.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EmailListener {

    private EmailSenderService emailSenderService;

    public EmailListener(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @KafkaListener(
            topics = "${kafka.topics}",
            containerFactory = "emailListenerContainerFactory" // see bean config in com.priyajit.ecommerce.service.config.kafka.KafkaConsumerConfig
    )
    public void emailListener(SendEmailDto dto) {

        emailSenderService.sendEmail(List.of(dto));
    }
}
