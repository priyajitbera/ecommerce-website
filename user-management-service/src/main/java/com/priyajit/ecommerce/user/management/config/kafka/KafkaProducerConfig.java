package com.priyajit.ecommerce.user.management.config.kafka;

import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.client.impl.KafkaEmailClientImplV1;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
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

    private Boolean KAKFA_PRODUCER_CONFIG_ENABLE;
    private String KAFKA_BOOTSTRAP_ADDRESS;
    private String KAFKA_TOPIC;
    private String KAFKA_MAX_REQUEST_SIZE;

    public KafkaProducerConfig(DbEnvironmentConfiguration dbEnvConfig) {

        this.KAKFA_PRODUCER_CONFIG_ENABLE = Boolean.valueOf(
                dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_PRODUCER_CONFIG_ENABLE)
        );

        log.info("KAFKA_PRODUCER_CONFIG_ENABLE is set to: {}",
                this.KAKFA_PRODUCER_CONFIG_ENABLE);

        if (this.KAKFA_PRODUCER_CONFIG_ENABLE) {
            this.KAFKA_BOOTSTRAP_ADDRESS = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_BOOTSTRAP_ADDRESS);
            this.KAFKA_TOPIC = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_TOPIC);
            this.KAFKA_MAX_REQUEST_SIZE = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_MAX_REQUEST_SIZE);
        }
    }

    @Bean("kafkaEmailClientV1")
    public EmailClient kafkaEmailClient() {
        if (!this.KAKFA_PRODUCER_CONFIG_ENABLE) {
            log.warn("KAFKA_PRODUCER_CONFIG_ENABLE is set to false, skipping {} configuration",
                    KafkaEmailClientImplV1.class.getName());
            return null;
        }
        return new KafkaEmailClientImplV1(kafkaTemplate(), KAFKA_TOPIC);
    }

    private KafkaTemplate<String, Serializable> kafkaTemplate() {

        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<String, Serializable> producerFactory() {

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
