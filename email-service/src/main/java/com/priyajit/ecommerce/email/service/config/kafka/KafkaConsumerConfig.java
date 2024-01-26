package com.priyajit.email.service.config.kafka;

import com.priyajit.email.service.dto.SendEmailDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-address}")
    private String KAFKA_BOOTSTRAP_ADDRESS;

    @Value("${kafka.group-id-config}")
    private String KAFKA_GROUP_ID_CONFIG;

    @Bean("emailListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SendEmailDto> emailListenerContainerFactory() {
        log.info("Configuring ConcurrentKafkaListenerContainerFactory");

        ConcurrentKafkaListenerContainerFactory<String, SendEmailDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    private ConsumerFactory<String, SendEmailDto> consumerFactory() {
        log.info("Configuring ConsumerFactory");

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_ADDRESS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_GROUP_ID_CONFIG);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> classMap = new HashMap<>();
        typeMapper.setIdClassMapping(classMap);
        typeMapper.addTrustedPackages("*");

        JsonDeserializer<SendEmailDto> jsonDeserializer = new JsonDeserializer<>(SendEmailDto.class);
        jsonDeserializer.setTypeMapper(typeMapper);
        jsonDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), jsonDeserializer);
    }
}
