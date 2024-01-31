package com.priyajit.product.ecommerce.catalog.service.service.impl;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.product.ecommerce.catalog.service.entity.Currency;
import com.priyajit.product.ecommerce.catalog.service.model.CurrencyModel;
import com.priyajit.product.ecommerce.catalog.service.repository.CurrencyRepository;
import com.priyajit.product.ecommerce.catalog.service.service.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CurrencyServiceImplV1 implements CurrencyService {

    private CurrencyRepository currencyRepository;

    public CurrencyServiceImplV1(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<CurrencyModel> createCurrencies(List<CreateCurrencyDto> dtos) {

        List<Currency> currencies = dtos.stream()
                .filter(Objects::nonNull)
                .map(this::createCurrencyFromDto)
                .collect(Collectors.toList());

        return currencyRepository.saveAllAndFlush(currencies).stream()
                .map(CurrencyModel::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyModel> findCurrencies(List<String> ids, List<String> names) {

        return currencyRepository.findAllByIdInOrNameIn(ids, names)
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
