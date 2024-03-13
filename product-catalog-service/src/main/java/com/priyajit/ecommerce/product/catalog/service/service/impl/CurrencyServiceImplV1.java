package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.CurrencyRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.service.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CurrencyServiceImplV1 implements CurrencyService {

    private CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod;

    public CurrencyServiceImplV1(CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod) {
        this.currencyRepositoryQueryMethod = currencyRepositoryQueryMethod;
    }

    @Override
    public List<CurrencyModel> createCurrencies(List<CreateCurrencyDto> dtos) {

        List<Currency> currencies = dtos.stream()
                .filter(Objects::nonNull)
                .map(this::createCurrencyFromDto)
                .collect(Collectors.toList());

        return currencyRepositoryQueryMethod.saveAllAndFlush(currencies).stream()
                .map(CurrencyModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyModel> findCurrencies(List<String> ids, List<String> names) {

        return currencyRepositoryQueryMethod.findAllByIdInOrNameIn(ids, names)
                .stream()
                .map(CurrencyModel::from)
                .collect(Collectors.toList());
    }

    private Currency createCurrencyFromDto(CreateCurrencyDto dto) {
        return Currency.builder()
                .name(dto.getName())
                .build();
    }
}
