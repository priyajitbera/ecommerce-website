package com.priyajit.ecommerce.user.management;

import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfigurationProperty;
import com.priyajit.ecommerce.user.management.repository.DbEnvironmentConfigurationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class UserManagementServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }

    private DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;

    @Override
    public void run(String... args) throws Exception {

//        DbEnvironmentConfiguration dbEnvConfig = DbEnvironmentConfiguration.builder().build();
//        dbEnvConfig.setVersion("1");
//        dbEnvConfig.setIsActive(true);
//        dbEnvConfig.setProperties(
//                List.of(
//                        // Kafka
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.KAFKA_PRODUCER_CONFIG_ENABLE)
//                                .value("true").build(),
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.KAFKA_BOOTSTRAP_ADDRESS)
//                                .value("localhost:39092").build(),
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.KAFKA_TOPIC)
//                                .value("user-email-delivery").build(),
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.KAFKA_MAX_REQUEST_SIZE)
//                                .value("20971520").build(),
//
//                        // AWS SQS
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.AWS_SQS_CONFIG_ENABLE)
//                                .value("true").build(),
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.AWS_SQS_REGION)
//                                .value("US_EAST_1").build(),
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.AWS_SQS_QUEUE_URL)
//                                .value("https://sqs.us-east-1.amazonaws.com/610757657796/ecommerce").build(),
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.AWS_SQS_ACCESS_KEY)
//                                .value("AKIAY4M76ITCB6CNELC5").build(),
//                        DbEnvironmentConfigurationProperty.builder().configuration(dbEnvConfig)
//                                .key(DbEnvironmentConfiguration.Keys.AWS_SQS_SECRET_KEY)
//                                .value("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").build()
//                )
//        );
//
//        dbEnvironmentConfigurationRepository.saveAndFlush(dbEnvConfig);
    }
}
