package com.priyajit.ecommerce.cart.service.redisrepository;

import com.priyajit.ecommerce.cart.service.redisdoc.FreecurrencyApiExchangeRates;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeRatesRedisRepository extends CrudRepository<FreecurrencyApiExchangeRates, String> {
}
