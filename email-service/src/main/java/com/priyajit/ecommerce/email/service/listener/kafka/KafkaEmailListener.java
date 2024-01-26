package com.priyajit.ecommerce.email.service.listener.kafka;

import com.priyajit.ecommerce.email.service.dto.SendEmailDto;
import com.priyajit.ecommerce.email.service.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
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
            @Nullable KafkaConsumer kafkaConsumer,
            EmailSenderService emailSenderService
    ) {
        this.kafkaConsumer = kafkaConsumer;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Method to check new email requests in Kafka queue and send using email service
     */
    @Async
    @Scheduled(fixedDelay = 10000) // polling at every 10 second
    public void listen() {
        if (kafkaConsumer == null) return;

        ConsumerRecords<String, SendEmailDto> records = kafkaConsumer.poll(1000);

        log.info("Found {} records", records.count());

        List<SendEmailDto> dtos = new ArrayList<>();
        records.forEach(record -> dtos.add(record.value()));

        if (dtos.size() > 0)
            emailSenderService.sendEmail(dtos);
    }
}
