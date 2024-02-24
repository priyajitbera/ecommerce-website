package com.priyajit.ecommerce.product.catalog.service.service;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;

import java.util.List;

public interface CurrencyService {
    List<CurrencyModel> createCurrencies(List<CreateCurrencyDto> dtos);

    List<CurrencyModel> findCurrencies(List<String> ids, List<String> names);
}
