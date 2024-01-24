package com.priyajit.email.service.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AwsSqsConfig {

    @Value("${aws.sqs.access-key}")
    private String AWS_SQS_ACCESS_KEY;
    @Value("${aws.sqs.secret-key}")
    private String AWS_SQS_SECRET_KEY;

    @Value("${aws.sqs.region}")
    private String AWS_SQS_REGION;

    @Bean
    @Primary
    public AmazonSQS amazonSQS() {

        Regions regions = Regions.fromName(AWS_SQS_REGION);
        return AmazonSQSClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(awsStaticCredentialsProvider())
                .build();
    }

    private AWSStaticCredentialsProvider awsStaticCredentialsProvider() {

        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        AWS_SQS_ACCESS_KEY,
                        AWS_SQS_SECRET_KEY
                )
        );
    }
}
