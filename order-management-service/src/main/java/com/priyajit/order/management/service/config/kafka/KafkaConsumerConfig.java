package com.priyajit.order.management.service.config.kafka;

import com.priyajit.order.management.service.event.dto.PaymentStatusConfirmationEventDto;
import com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration.Keys;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Bean("paymentStatusConfirmationEventConsumer")
    public KafkaConsumer<String, PaymentStatusConfirmationEventDto> kafkaConsumer(DbEnvironmentConfiguration configuration) {

        var enableConsumerConfig = Boolean.parseBoolean(configuration.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_CONSUMER_CONFIG_ENABLE));
        if (!enableConsumerConfig) {
            log.warn("Property {} is set to {}, skipping 'paymentStatusConfirmationEventConsumer' bean configuration",
                    Keys.KAFKA_CONSUMER_CONFIG_ENABLE, enableConsumerConfig);
            return null;
        }

        var KAFKA_BOOTSTRAP_SERVER = configuration.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_BOOTSTRAP_ADDRESS);
        var KAFKA_GROUP_ID_CONFIG = configuration.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_GROUP_ID_CONFIG);
        var KAFKA_TOPIC = configuration.getProperty(Keys.KAFKA_TOPIC_PAYMENT_STATUS_CONFIRMATION);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVER);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_GROUP_ID_CONFIG);

        var typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.addTrustedPackages("*");

        var deserializer = new JsonDeserializer<>(PaymentStatusConfirmationEventDto.class);
        deserializer.setTypeMapper(typeMapper);
        KafkaConsumer<String, PaymentStatusConfirmationEventDto> kafkaConsumer = new KafkaConsumer<>(
                config,
                new StringDeserializer(),
                deserializer
        );

        kafkaConsumer.subscribe(List.of(KAFKA_TOPIC));
        return kafkaConsumer;
    }
}
