package com.priyajit.order.management.service.listener.kafka;

import com.priyajit.order.management.service.event.dto.PaymentStatusConfirmationEventDto;
import com.priyajit.order.management.service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentStatusConfirmationEventListenerKafka {

    //    @Qualifier("paymentStatusConfirmationEventConsumer")
    private KafkaConsumer<String, PaymentStatusConfirmationEventDto> kafkaConsumer;
    private OrderService orderService;

    public PaymentStatusConfirmationEventListenerKafka(
            KafkaConsumer<String, PaymentStatusConfirmationEventDto> kafkaConsumer,
            OrderService orderService) {

        this.kafkaConsumer = kafkaConsumer;
        this.orderService = orderService;
    }

    /**
     * Method to check new payment-status-confirmation-event requests in Kafka queue and send using email service
     */
    @Async
    @Scheduled(fixedDelay = 10000) // polling at every 10 second
    public void listen() {
        if (kafkaConsumer == null) return;

        ConsumerRecords<String, PaymentStatusConfirmationEventDto> records = kafkaConsumer.poll(1000);

        log.info("Found {} records", records.count());

        records.forEach(record -> {
            var eventDto = record.value();
            try {
                orderService.updatePaymentStatus(eventDto);
                log.info("Successfully updated payment status for paymentId: {}", eventDto.getPaymentId());
            } catch (Throwable e) {
                log.error("Failed to update payment status for paymentId: {}, {}",
                        eventDto.getPaymentId(), e.getMessage());
                e.printStackTrace();
            }
        });

    }
}
