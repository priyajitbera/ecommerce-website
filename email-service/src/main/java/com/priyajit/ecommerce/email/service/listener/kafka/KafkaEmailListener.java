package com.priyajit.ecommerce.email.service.listener.kafka;

import com.priyajit.ecommerce.email.service.dto.SendEmailDto;
import com.priyajit.ecommerce.email.service.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KafkaEmailListener {

    private KafkaConsumer<String, SendEmailDto> kafkaConsumer;
    private EmailSenderService emailSenderService;

    public KafkaEmailListener(
            KafkaConsumer kafkaConsumer,
            EmailSenderService emailSenderService
    ) {
        this.kafkaConsumer = kafkaConsumer;
        this.emailSenderService = emailSenderService;
    }

    @Scheduled(fixedDelay = 10000) // polling at every 10 second
    public void emailListener(SendEmailDto dto) {

        ConsumerRecords<String, SendEmailDto> records = kafkaConsumer.poll(1000);

        List<SendEmailDto> dtos = new ArrayList<>();

        records.forEach(record -> dtos.add(record.value()));

        emailSenderService.sendEmail(dtos);
    }
}
