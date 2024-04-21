package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import com.priyajit.ecommerce.product.catalog.service.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/currency/v1")
public class CurrencyController {

    private CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    public List<CurrencyModel> createCurrencies(
            @RequestBody List<CreateCurrencyDto> dtos
    ) {
        return currencyService.createCurrencies(dtos);
    }

    @GetMapping
    public List<CurrencyModel> findCurrencies(
            @RequestParam(name = "id", required = false) List<String> ids,
            @RequestParam(name = "name", required = false) List<String> names
    ) {
        return currencyService.findCurrencies(ids, names);
    }

    @GetMapping("find-one")
    public CurrencyModel findOneById(
            @RequestParam(name = "id") String id
    ) {
        return currencyService.findById(id);
    }
}
