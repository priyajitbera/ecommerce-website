package com.priyajit.ecommerce.cart.service.config;


import com.freecurrencyapi.ApiClient;
import com.freecurrencyapi.api.FreecurrencyExchangeRatesApi;
import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration.Keys.FREECURRENCY_SERVICE_API_KEY;
import static com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration.Keys.FREECURRENCY_SERVICE_BASE_URL;

@Slf4j
@Configuration
public class FreeCurrencyApiClientConfig {

    @Bean("freeCurrencyApiClient")
    public ApiClient freeCurrencyApiClient(DbEnvironmentConfiguration configuration) {
        log.info("Configuring freeCurrencyApiClient baseUrl: {}", configuration.getProperty(FREECURRENCY_SERVICE_BASE_URL));
        var apiClient = new ApiClient();
        apiClient.setBasePath(configuration.getProperty(FREECURRENCY_SERVICE_BASE_URL));
        apiClient.addDefaultHeader("apikey", configuration.getProperty(FREECURRENCY_SERVICE_API_KEY));
        return apiClient;
    }

    @Bean
    public FreecurrencyExchangeRatesApi exchangeRatesApi(
            @Qualifier("freeCurrencyApiClient")
            ApiClient apiClient
    ) {
        return new FreecurrencyExchangeRatesApi(apiClient);
    }
}
