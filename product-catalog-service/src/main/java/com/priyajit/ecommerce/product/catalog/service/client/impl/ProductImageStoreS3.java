package com.priyajit.ecommerce.product.catalog.service.client.impl;

import com.priyajit.ecommerce.product.catalog.service.client.ProductImageStore;
import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
public class ProductImageStoreS3 implements ProductImageStore {

    private S3Client s3Client;
    private DbEnvironmentConfiguration configuration;

    public ProductImageStoreS3(S3Client s3Client, DbEnvironmentConfiguration configuration) {
        this.s3Client = s3Client;
        this.configuration = configuration;
    }

    @Override
    public String store(String key, MultipartFile file) {
        if (key == null || file == null)
            throw new IllegalArgumentException("Non null values expected for key and file");

        try {
            String BUCKET_NAME = configuration.getProperty(DbEnvironmentConfiguration.Keys.AWS_S3_BUCKET_NAME);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

            var requestBody = RequestBody.fromBytes(file.getBytes());

            log.info("Before saving file to S3 bucket");
            var response = s3Client.putObject(request, requestBody);
            log.info("After saving file to S3 bucket | {}", response.toString());

            // form url and return
            String S3_REGION_NAME = configuration.getProperty(DbEnvironmentConfiguration.Keys.AWS_S3_REGION);
            return createS3ObjectUrl(S3_REGION_NAME, BUCKET_NAME, key);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createS3ObjectUrl(String REGION_NAME, String BUCKET_NAME, String KEY) {
        // format: https://<REGION_NAME>.s3.<BUCKET_NAME>.amazonaws.com/<KEY>
        return String.format("https://%s.s3.%s.amazonaws.com/%s", REGION_NAME, BUCKET_NAME, KEY);
    }
}
