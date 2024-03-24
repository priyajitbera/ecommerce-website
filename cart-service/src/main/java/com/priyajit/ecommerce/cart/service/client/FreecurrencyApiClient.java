package com.priyajit.ecommerce.cart.service.client;

import com.priyajit.ecommerce.cart.service.client.model.FreecurrencyApiModel;

public interface FreecurrencyApiClient {

    FreecurrencyApiModel getExchangeRate(String baseCurrency);
}
