package com.priyajit.ecommerce.cart.service.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreecurrencyApiModel {

    // map of currency to exchange rate
    private Map<String, BigDecimal> data;
}
