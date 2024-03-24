package com.priyajit.ecommerce.cart.service.client.impl;

import com.priyajit.ecommerce.cart.service.client.FreecurrencyApiClient;
import com.priyajit.ecommerce.cart.service.client.model.FreecurrencyApiModel;
import com.priyajit.ecommerce.cart.service.exception.InvalidCurrencyNameException;
import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class FreecurrencyApiClientImplV1 implements FreecurrencyApiClient {

    private DbEnvironmentConfiguration configuration;

    public FreecurrencyApiClientImplV1(DbEnvironmentConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public FreecurrencyApiModel getExchangeRate(String baseCurrency) {

        String BASE_URL = configuration.getProperty(DbEnvironmentConfiguration.Keys.FREECURRENCY_SERVICE_BASE_URL);
        String API_KEY = configuration.getProperty(DbEnvironmentConfiguration.Keys.FREECURRENCY_SERVICE_API_KEY);
        RestClient restClient = RestClient.create(BASE_URL);

        try {
            log.info("Before calling freecurrencyapi.com API");
            var response = restClient.get()
                    .uri(uri -> uri
                            .path("/v1/latest")
                            .queryParam("apikey", API_KEY)
                            .queryParam("base_currency", baseCurrency)
                            .build())
                    .retrieve()
                    .toEntity(FreecurrencyApiModel.class);
            log.info("After calling freecurrencyapi.com API, status:{}", response.getStatusCode());
            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            } else {
                throw new RuntimeException(
                        String.format("Error occurred while calling freecurrencyapi.com API, expected status: 200 OK, but got: %s",
                                response.getStatusCode())
                );
            }
        }
        // API returns '422 Unprocessable Entity' when given invalid/unknown currency name as base_currency
        catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new InvalidCurrencyNameException(baseCurrency);
            } else throw new RuntimeException("Error occurred while calling freecurrencyapi.com API", e);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while calling freecurrencyapi.com API", e);
        }
    }
}
