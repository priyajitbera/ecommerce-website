package com.priyajit.ecommerce.payment.service.config.kafka;


import com.priyajit.ecommerce.payment.service.client.PaymentStatusConfirmationEventProducerClient;
import com.priyajit.ecommerce.payment.service.client.impl.PaymentStatusConfirmationEventProducerClientKafka;
import com.priyajit.ecommerce.payment.service.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.payment.service.exception.InvalidConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaProducerConfig {

    private DbEnvironmentConfiguration configuration;

    @Bean("paymentStatusConfirmationEventProducerClientKafka")
    public PaymentStatusConfirmationEventProducerClient paymentStatusConfirmationEventProducerClient(
            DbEnvironmentConfiguration configuration
    ) {
        var enableProducerConfig = Boolean.parseBoolean(configuration.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_PRODUCER_CONFIG_ENABLE));
        if (!enableProducerConfig) {
            log.info("Property {} is set to {}, skipping the 'paymentStatusConfirmationEventProducerClientKafka' bean configuration",
                    DbEnvironmentConfiguration.Keys.KAFKA_PRODUCER_CONFIG_ENABLE, enableProducerConfig);
            return null;
        }

        // validate topicName
        String topicName = configuration.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_PAYMENT_STATUS_CONFIRMATION_TOPIC);
        if (topicName == null) {
            throw new InvalidConfigurationException(String.format("Value for configuration property with key %s is not set",
                    DbEnvironmentConfiguration.Keys.KAFKA_PAYMENT_STATUS_CONFIRMATION_TOPIC));
        }
        return new PaymentStatusConfirmationEventProducerClientKafka(
                kafkaTemplate(configuration),
                topicName
        );
    }

    private KafkaTemplate<String, Serializable> kafkaTemplate(DbEnvironmentConfiguration configuration) {
        return new KafkaTemplate<>(producerFactory(configuration));
    }

    private ProducerFactory<String, Serializable> producerFactory(DbEnvironmentConfiguration configuration) {

        var KAFKA_BOOTSTRAP_ADDRESS = configuration.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_BOOTSTRAP_ADDRESS);
        var KAFKA_MAX_REQUEST_SIZE = configuration.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_MAX_REQUEST_SIZE);

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_ADDRESS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, KAFKA_MAX_REQUEST_SIZE);

        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }
}
