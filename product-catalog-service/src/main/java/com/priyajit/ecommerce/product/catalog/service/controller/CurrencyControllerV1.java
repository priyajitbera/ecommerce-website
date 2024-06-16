package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import com.priyajit.ecommerce.product.catalog.service.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/currency")
@CrossOrigin("*")
public class CurrencyControllerV1 {

    private CurrencyService currencyService;

    public CurrencyControllerV1(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    public ResponseEntity<List<CurrencyModel>> createCurrencies(
            @RequestBody List<CreateCurrencyDto> dtos
    ) {
        return ok(currencyService.createCurrencies(dtos));
    }

    @GetMapping
    public ResponseEntity<List<CurrencyModel>> findCurrencies(
            @RequestParam(name = "id", required = false) List<String> ids,
            @RequestParam(name = "name", required = false) List<String> names
    ) {
        return ok(currencyService.findCurrencies(ids, names));
    }

    @GetMapping("find-one")
    public ResponseEntity<CurrencyModel> findOneById(
            @RequestParam(name = "id") String id
    ) {
        return ok(currencyService.findById(id));
    }
}
