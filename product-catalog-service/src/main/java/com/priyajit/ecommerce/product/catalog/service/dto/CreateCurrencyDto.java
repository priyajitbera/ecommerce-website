package com.priyajit.ecommerce.product.catalog.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCurrencyDto {

    private String id;
    private String name;
    private String symbol;
    private String shortSymbol;
}
