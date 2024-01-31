package com.priyajit.product.ecommerce.catalog.service.model;

import com.priyajit.product.ecommerce.catalog.service.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyModel {

    private String id;
    private ZonedDateTime createdOn;
    private ZonedDateTime lastModifiedOn;
    private String name;

    public static CurrencyModel from(Currency currency) {
        if (currency == null) return null;

        return CurrencyModel.builder()
                .id(currency.getId())
                .createdOn(currency.getCreatedOn())
                .lastModifiedOn(currency.getLastModifiedOn())
                .name(currency.getName())
                .build();
    }
}
