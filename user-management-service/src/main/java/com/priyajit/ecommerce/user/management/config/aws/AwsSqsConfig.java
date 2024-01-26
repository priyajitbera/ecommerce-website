package com.priyajit.ecommerce.user.management.config.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.client.impl.AwsSqsEmailClientV1;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsSqsConfig {

    private Boolean AWS_SQS_CONFIG_ENABLE;
    private String AWS_SQS_REGION;
    private String AWS_SQS_QUEUE_URL;
    private String AWS_SQS_ACCESS_KEY;
    private String AWS_SQS_SECRET_KEY;

    public AwsSqsConfig(DbEnvironmentConfiguration dbEnvConfig) {

        this.AWS_SQS_CONFIG_ENABLE
                = Boolean.valueOf(dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_CONFIG_ENABLE));

        log.info("AWS_SQS_CONFIG_ENABLE is set to: {}", AWS_SQS_CONFIG_ENABLE);

        if (this.AWS_SQS_CONFIG_ENABLE) {
            this.AWS_SQS_REGION = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_REGION);
            this.AWS_SQS_QUEUE_URL = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_QUEUE_URL);
            this.AWS_SQS_ACCESS_KEY = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_ACCESS_KEY);
            this.AWS_SQS_SECRET_KEY = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_SECRET_KEY);
        }
    }

    @Bean("awsSqsEmailClientV1")
    public EmailClient awsSqsEmailClientV1() {
        if (!AWS_SQS_CONFIG_ENABLE) {
            log.warn("AWS_SQS_CONFIG_ENABLE is set to false, skipping {} bean configuration",
                    AwsSqsEmailClientV1.class.getName());
            return null;
        }
        return new AwsSqsEmailClientV1(amazonSQS(), AWS_SQS_QUEUE_URL);
    }

    private AmazonSQS amazonSQS() {
        if (!AWS_SQS_CONFIG_ENABLE) return null;

        Regions regions = Regions.fromName(AWS_SQS_REGION);

        return AmazonSQSClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(awsSqsCredentialProvider())
                .build();
    }

    private AWSCredentialsProvider awsSqsCredentialProvider() {
        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        AWS_SQS_ACCESS_KEY,
                        AWS_SQS_SECRET_KEY
                )
        );
    }
}
