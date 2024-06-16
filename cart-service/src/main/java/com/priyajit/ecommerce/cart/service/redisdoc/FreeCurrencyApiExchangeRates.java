package com.priyajit.ecommerce.cart.service.redisdoc;

import com.freecurrencyapi.model.ExchangeRatesResponseSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "exchangeRates", timeToLive = 86400) // TTL: 1 Day
public class FreeCurrencyApiExchangeRates {

    @Id
    private String baseCurrency;
    private ZonedDateTime createdOn;
    private ExchangeRatesResponseSchema exchangeRatesResponseSchema;
}
