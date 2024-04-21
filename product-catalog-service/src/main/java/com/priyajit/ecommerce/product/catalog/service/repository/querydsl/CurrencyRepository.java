package com.priyajit.ecommerce.product.catalog.service.repository.querydsl;

import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import org.springframework.lang.Nullable;

import java.util.List;

public interface CurrencyRepository {

    List<Currency> searchCurrency(
            @Nullable List<String> ids,
            @Nullable List<String> name
    );
}
