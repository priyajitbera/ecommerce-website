package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import com.priyajit.ecommerce.product.catalog.service.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.when;


@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplV1Test {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyServiceImplV1 currencyService;

    @Test
    void createCurrencies_sucess() {

        // arrange
        List<CreateCurrencyDto> dtos = List.of(
                CreateCurrencyDto.builder().name("INR").build(),
                CreateCurrencyDto.builder().name("DOLLAR").build()
        );

        // mock method calls
        when(currencyRepository.saveAllAndFlush(Mockito.anyList()))
                .then(i -> mockSave(i.getArgument(0, List.class)));

        // act
        List<CurrencyModel> currencyModels = currencyService.createCurrencies(dtos);

        // assertion
        assertNotNull(currencyModels);
        assertEquals(dtos.size(), currencyModels.size());
        assertNotNull(currencyModels.get(0).getId());
        assertNotNull(currencyModels.get(1).getId());
        assertNotNull(currencyModels.get(0).getCreatedOn());
        assertNotNull(currencyModels.get(1).getCreatedOn());
        assertNotNull(currencyModels.get(0).getLastModifiedOn());
        assertNotNull(currencyModels.get(1).getLastModifiedOn());
        assertEquals(dtos.get(0).getName(), currencyModels.get(0).getName());
        assertEquals(dtos.get(1).getName(), currencyModels.get(1).getName());
    }

    @Test
    void findCurrencies_withIds_success() {

        // arrange
        String id1 = "ID-INR";
        String id2 = "ID-DOLLAR";
        String name1 = "INR";
        String name2 = "DOLLAR";
        List<String> ids = List.of(id1, id2);
        Currency currency1 = Currency.builder().id(id1).name(name1).build();
        Currency currency2 = Currency.builder().id(id2).name(name2).build();
        List<Currency> currencies = List.of(currency1, currency2);

        // mock methods
        when(currencyRepository.findAllByIdInOrNameIn(ids, null))
                .thenReturn(currencies);
        // act
        List<CurrencyModel> currencyModels = currencyService.findCurrencies(ids, null);

        // assert
        assertNotNull(currencyModels);
        assertEquals(id1, currencyModels.get(0).getId());
        assertEquals(id2, currencyModels.get(1).getId());
        assertEquals(name1, currencyModels.get(0).getName());
        assertEquals(name2, currencyModels.get(1).getName());
    }

    @Test
    void findCurrencies_withNames_success() {

        // arrange
        String id1 = "ID-INR";
        String id2 = "ID-DOLLAR";
        String name1 = "INR";
        String name2 = "DOLLAR";
        List<String> names = List.of(name1, name2);
        Currency currency1 = Currency.builder().id(id1).name(name1).build();
        Currency currency2 = Currency.builder().id(id2).name(name2).build();
        List<Currency> currencies = List.of(currency1, currency2);

        // mock methods
        when(currencyRepository.findAllByIdInOrNameIn(null, names))
                .thenReturn(currencies);
        // act
        List<CurrencyModel> currencyModels = currencyService.findCurrencies(null, names);

        // assert
        assertNotNull(currencyModels);
        assertEquals(id1, currencyModels.get(0).getId());
        assertEquals(id2, currencyModels.get(1).getId());
        assertEquals(name1, currencyModels.get(0).getName());
        assertEquals(name2, currencyModels.get(1).getName());
    }

    /**
     * Helper method to mock List of currency objects by setting a random Id, createdOn and lastModified fields
     *
     * @param currencies
     * @return
     */
    private List<Currency> mockSave(List<Currency> currencies) {

        return currencies.stream()
                .map(this::mockSave)
                .collect(Collectors.toList());
    }

    /**
     * Helper method mocks saving of Currency object by setting a random Id, createdOn and lastModified fields
     *
     * @param currency
     * @return
     */
    private Currency mockSave(Currency currency) {

        currency.setId(UUID.randomUUID().toString());
        currency.setCreatedOn(ZonedDateTime.now());
        currency.setLastModifiedOn(ZonedDateTime.now());

        return currency;
    }


}