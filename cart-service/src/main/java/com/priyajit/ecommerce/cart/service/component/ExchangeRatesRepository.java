package com.priyajit.ecommerce.cart.service.component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface ExchangeRatesRepository {

    Map<String, BigDecimal> getExchangeRates(java.lang.String baseCurrency);

    Optional<BigDecimal> getExchangeRate(String baseCurrency, String targetCurrency);
}
