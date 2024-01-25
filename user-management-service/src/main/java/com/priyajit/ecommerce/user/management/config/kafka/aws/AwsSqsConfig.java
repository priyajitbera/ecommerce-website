package com.priyajit.ecommerce.user.management.config.kafka.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class AwsSqsConfig {

    @Value("${aws.sqs.region}")
    private String AWS_SQS_REGION;

    @Value("${aws.sqs.access-key}")
    private String AWS_SQS_ACCESS_KEY;
    @Value("${aws.sqs.secret-key}")
    private String AWS_SQS_SCECRET_KEY;

    @Bean
    public AmazonSQS amazonSQS() {

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
                        AWS_SQS_SCECRET_KEY
                )
        );
    }
}
