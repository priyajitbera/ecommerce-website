package com.priyajit.email.service.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsSesConfig {

    @Value("${aws.ses.access.key}")
    private String AWS_SES_ACCESS_KEY;

    @Value("${aws.ses.secret.key}")
    private String AWS_SES_SECRET_KEY;

    @Value("${aws.ses.region}")
    private Regions AWS_SES_REGION;

    @Value("${aws.ses.sender}")
    private String AWS_SES_SENDER;

    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {

        BasicAWSCredentials credentials = new BasicAWSCredentials(
                AWS_SES_ACCESS_KEY,
                AWS_SES_SECRET_KEY
        );
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(AWS_SES_REGION)
                .build();
    }
}
