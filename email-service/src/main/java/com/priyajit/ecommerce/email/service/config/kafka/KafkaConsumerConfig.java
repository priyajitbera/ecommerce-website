package com.priyajit.ecommerce.email.service.config.kafka;

import com.priyajit.ecommerce.email.service.dto.SendEmailDto;
import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private Boolean kafkaConsumerConfigEnable;
    private String kafkaBootstrapAddress;
    private String kafkaGroupIdConfig;
    private String kafkaTopic;

    public KafkaConsumerConfig(DbEnvironmentConfiguration dbEnvConfig) {
        this.kafkaConsumerConfigEnable = Boolean.valueOf(
                dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_CONSUMER_CONFIG_ENABLE)
        );
        if (this.kafkaConsumerConfigEnable) {
            this.kafkaBootstrapAddress = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_BOOTSTRAP_ADDRESS);
            this.kafkaGroupIdConfig = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_GROUP_ID_CONFIG);
            this.kafkaTopic = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.KAFKA_TOPIC);
        }
    }

    @Bean
    public KafkaConsumer<String, SendEmailDto> kafkaConsumer() {

        if (!this.kafkaConsumerConfigEnable) {
            log.warn("kafkaConsumerConfigEnable is set to false, skipping kafkaConsumer configuration");
            return null;
        }

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupIdConfig);
        KafkaConsumer<String, SendEmailDto> kafkaConsumer = new KafkaConsumer<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(SendEmailDto.class)
        );

        kafkaConsumer.subscribe(List.of(this.kafkaTopic));
        return kafkaConsumer;
    }

    //    @Bean("emailListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SendEmailDto> emailListenerContainerFactory() {

        if (!this.kafkaConsumerConfigEnable) {
            log.warn("kafkaConsumerConfigEnable is set to false, skipping emailListenerContainerFactory configuration");
            return null;
        }

        ConcurrentKafkaListenerContainerFactory<String, SendEmailDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    private ConsumerFactory<String, SendEmailDto> consumerFactory() {

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupIdConfig);

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
