package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import com.priyajit.ecommerce.product.catalog.service.exception.CurrencyNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.CurrencyRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.CurrencyRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.service.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CurrencyServiceImplV1 implements CurrencyService {

    private CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod;
    private CurrencyRepository currencyRepository;

    public CurrencyServiceImplV1(CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod, CurrencyRepository currencyRepository) {
        this.currencyRepositoryQueryMethod = currencyRepositoryQueryMethod;
        this.currencyRepository = currencyRepository;
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

        return currencyRepository.searchCurrency(ids, names)
                .stream()
                .map(CurrencyModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public CurrencyModel findById(String id) {
        var currency = currencyRepositoryQueryMethod.findById(id)
                .orElseThrow(CurrencyNotFoundException.supplier(id));

        return CurrencyModel.from(currency);
    }

    private Currency createCurrencyFromDto(CreateCurrencyDto dto) {
        return Currency.builder()
                .id(dto.getId())
                .name(dto.getName())
                .symbol(dto.getSymbol())
                .shortSymbol(dto.getShortSymbol())
                .build();
    }
}
