package com.priyajit.ecommerce.product.catalog.service.config;

import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Bean
    public S3Client s3Client(DbEnvironmentConfiguration configuration) {

        var awsCredentials = AwsBasicCredentials
                .create(
                        configuration.getProperty(DbEnvironmentConfiguration.Keys.AWS_S3_ACCESS_KEY),
                        configuration.getProperty(DbEnvironmentConfiguration.Keys.AWS_S3_SECRET_KEY)
                );

        var region = Region.of(configuration.getProperty(DbEnvironmentConfiguration.Keys.AWS_S3_REGION));

        return S3Client.builder()
                .credentialsProvider(() -> awsCredentials)
                .region(region)
                .build();
    }
}
