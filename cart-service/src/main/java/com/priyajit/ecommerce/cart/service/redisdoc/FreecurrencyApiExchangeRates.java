package com.priyajit.ecommerce.cart.service.redisdoc;

import com.priyajit.ecommerce.cart.service.client.model.FreecurrencyApiModel;
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
public class FreecurrencyApiExchangeRates {

    @Id
    private String baseCurrency;
    private ZonedDateTime createdOn;
    private FreecurrencyApiModel rates;
}
