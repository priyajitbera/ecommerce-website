package com.priyajit.order.management.service.client.impl;

import com.priyajit.order.management.service.client.PaymentServiceClient;
import com.priyajit.order.management.service.client.dto.CreatePaymentDto;
import com.priyajit.order.management.service.client.model.PaymentModel;
import com.priyajit.order.management.service.client.model.Response;
import com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class PaymentServiceClientImplV1 implements PaymentServiceClient {

    private DbEnvironmentConfiguration configuration;

    public PaymentServiceClientImplV1(DbEnvironmentConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public PaymentModel createPayment(CreatePaymentDto dto) {
        var BASE_URL = configuration.getProperty(DbEnvironmentConfiguration.Keys.PAYMENT_SERVICE_BASE_URL);
        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();

        ResponseEntity<Response<PaymentModel>> response;
        try {
            log.info("Before calling payment-service API");
            response = restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/v1/payment").build())
                    .body(dto)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Response<PaymentModel>>() {
                    });
            log.info("After calling payment-service API, status:{}", response.getStatusCode());
        }
        // error
        catch (HttpClientErrorException e) {
            log.error("After calling payment-service API, status:{}", e.getStatusCode());
            throw new RuntimeException(String.format("Error occurred while calling product-catalog-service API status: %s, %s",
                    e.getStatusCode(), e.getMessage()), e);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while calling product-catalog-service API ", e);
        }

        if (response.getBody().getError() != null) {
            log.error("Provided error in response body: {}", response.getBody().getError());
        }
        if (HttpStatus.OK == response.getStatusCode()) {
            return response.getBody().getData();
        } else
            throw new RuntimeException(String.format("Error occurred while calling product-catalog-service API, expected status: 200 OK, but got %s",
                    response.getStatusCode()));
    }
}
