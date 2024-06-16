package com.priyajit.ecommerce.cart.service.component.impl;

import com.freecurrencyapi.api.FreecurrencyExchangeRatesApi;
import com.priyajit.ecommerce.cart.service.component.ExchangeRatesRepository;
import com.priyajit.ecommerce.cart.service.redisdoc.FreeCurrencyApiExchangeRates;
import com.priyajit.ecommerce.cart.service.redisrepository.ExchangeRatesRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ExchangeRatesRatesRepositoryImpl implements ExchangeRatesRepository {

    private FreecurrencyExchangeRatesApi exchangeRatesApi;
    private ExchangeRatesRedisRepository exchangeRatesRedisRepository;

    public ExchangeRatesRatesRepositoryImpl(
            FreecurrencyExchangeRatesApi exchangeRatesApi,
            ExchangeRatesRedisRepository exchangeRatesRedisRepository
    ) {
        this.exchangeRatesApi = exchangeRatesApi;
        this.exchangeRatesRedisRepository = exchangeRatesRedisRepository;
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates(String baseCurrency) {
        try {
            log.info("[getExchangeRates] Before calling exchangeRatesRedisRepository.findById");
            var exChangeRatesOpt = exchangeRatesRedisRepository.findById(baseCurrency);
            if (exChangeRatesOpt.isPresent())
                return exChangeRatesOpt.get().getExchangeRatesResponseSchema().getData();
        } catch (Throwable e) {
            log.error("[getExchangeRates] Error occurred while calling exchangeRatesRedisRepository.findById");
            e.printStackTrace();
        }
        var response = exchangeRatesApi.fetchByBaseCurrency(baseCurrency);
        // cache the response
        var exchangeRates = FreeCurrencyApiExchangeRates.builder()
                .createdOn(ZonedDateTime.now())
                .baseCurrency(baseCurrency)
                .exchangeRatesResponseSchema(response).build();
        saveExchangeRatesToRedisOptimistic(exchangeRates);

        return response.getData();
    }

    @Override
    public Optional<BigDecimal> getExchangeRate(String baseCurrency, String toCurrency) {
        var exchangeRates = getExchangeRates(baseCurrency);
        return exchangeRates.containsKey(toCurrency)
                ? Optional.of(exchangeRates.get(toCurrency))
                : Optional.empty();
    }

    /**
     * Helper method to save FreeCurrencyApiExchangeRates to Redis
     *
     * @param freeCurrencyApiExchangeRates
     */
    private void saveExchangeRatesToRedisOptimistic(FreeCurrencyApiExchangeRates freeCurrencyApiExchangeRates) {
        try {
            CompletableFuture.supplyAsync(() -> exchangeRatesRedisRepository.save(freeCurrencyApiExchangeRates))
                    .thenAccept(saved -> log.info("Saved ExchangeRates object to Redis successfully"))
                    .exceptionally(e -> {
                        log.error("Error occurred while saving ExchangeRates object to Redis, {}", e.getMessage());
                        e.printStackTrace();
                        return null;
                    });
        } catch (Throwable e) {
            log.error("Error occurred while saving FreeCurrencyApiExchangeRates object to Redis, {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
