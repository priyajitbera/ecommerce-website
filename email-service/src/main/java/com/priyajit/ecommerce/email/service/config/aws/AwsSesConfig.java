package com.priyajit.ecommerce.email.service.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsSesConfig {

    private String awsSesRegion;
    private String awsSesAccessKey;
    private String awsSesSecretKey;

    public AwsSesConfig(DbEnvironmentConfiguration dbEnvConfig) {

        this.awsSesRegion = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SES_REGION);
        this.awsSesAccessKey = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SES_ACCESS_KEY);
        this.awsSesSecretKey = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.AWS_SES_SECRET_KEY);
    }

    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {

        BasicAWSCredentials credentials = new BasicAWSCredentials(
                awsSesAccessKey,
                awsSesSecretKey
        );

        Regions regions = Regions.fromName(awsSesRegion);

        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(regions)
                .build();
    }
}
