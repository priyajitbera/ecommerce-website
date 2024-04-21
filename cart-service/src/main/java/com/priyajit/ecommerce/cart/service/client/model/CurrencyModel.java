package com.priyajit.ecommerce.cart.service.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyModel {

    private String id;
    private String name;
    private String symbol;
    private String shortSymbol;
}
