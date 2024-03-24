package com.priyajit.ecommerce.cart.service.domain;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class Currencies {

    public static Set<String> currencies;

    public static Set<String> getCurrencies() {
        if (currencies == null) {
            synchronized (Currencies.class) {
                if (currencies == null) {
                    currencies = Currency.getAvailableCurrencies().stream()
                            .map(Currency::getCurrencyCode)
                            // The code XXX is not an actual currency, is used to denote a "transaction" involving no currency, exclude it
                            .filter(currency -> !"XXX".equals(currency))
                            .collect(Collectors.toSet());
                }
            }

        }
        return currencies;
    }
}
