package com.priyajit.order.management.service;

import com.priyajit.order.management.service.mongorepository.DbEnvironmentConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderManagementServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OrderManagementServiceApplication.class, args);
    }

    @Autowired
    private DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;

    @Override
    public void run(String... args) throws Exception {

//        var config = dbEnvironmentConfigurationRepository.findById("66042c01be6258629d499f0d").get();
//        config.getProperties()
//                .add(DbEnvironmentConfiguration.Property.builder()
//                        .key(DbEnvironmentConfiguration.Keys.KAFKA_CONSUMER_CONFIG_ENABLE)
//                        .value("true")
//                        .build());
//        config.getProperties()
//                .add(DbEnvironmentConfiguration.Property.builder()
//                        .key(DbEnvironmentConfiguration.Keys.KAFKA_TOPIC_PAYMENT_STATUS_CONFIRMATION)
//                        .value("payment-status-confirmation")
//                        .build());
//        config.getProperties()
//                .add(DbEnvironmentConfiguration.Property.builder()
//                        .key(DbEnvironmentConfiguration.Keys.KAFKA_BOOTSTRAP_ADDRESS)
//                        .value("localhost:39092")
//                        .build());
//        config.getProperties()
//                .add(DbEnvironmentConfiguration.Property.builder()
//                        .key(DbEnvironmentConfiguration.Keys.KAFKA_GROUP_ID_CONFIG)
//                        .value("PAYMENT_STATUS_CONFIRMATION_EVENT_CONSUMER_GROUP")
//                        .build());
//        dbEnvironmentConfigurationRepository.save(config);
    }
}
