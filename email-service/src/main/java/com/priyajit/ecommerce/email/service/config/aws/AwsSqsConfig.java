package com.priyajit.ecommerce.email.service.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsSqsConfig {

    private Boolean awsSqsConfigEnable;
    private String awsSqsAccessKey;
    private String awsSqsSecretKey;
    private String awsSqsRegion;

    public AwsSqsConfig(DbEnvironmentConfiguration dbEnvConfig) {
        this.awsSqsConfigEnable = Boolean.valueOf(
                dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_CONFIG_ENABLE)
        );
        if (this.awsSqsConfigEnable) {
            this.awsSqsRegion = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_REGION);
            this.awsSqsAccessKey = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_ACCESS_KEY);
            this.awsSqsSecretKey = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SQS_SECRET_KEY);
        }
    }

    @Bean
    public AmazonSQS amazonSQS() {
        if (!this.awsSqsConfigEnable) {
            log.warn("awsSqsConfigEnable is set to false, skipping amazonSQS bean configuration");
            return null;
        }

        Regions regions = Regions.fromName(awsSqsRegion);

        return AmazonSQSClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(awsStaticCredentialsProvider())
                .build();
    }

    private AWSStaticCredentialsProvider awsStaticCredentialsProvider() {

        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        awsSqsAccessKey,
                        awsSqsSecretKey
                )
        );
    }
}
