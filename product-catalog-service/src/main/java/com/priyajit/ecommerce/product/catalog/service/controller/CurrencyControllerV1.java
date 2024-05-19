package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import com.priyajit.ecommerce.product.catalog.service.model.Response;
import com.priyajit.ecommerce.product.catalog.service.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.priyajit.ecommerce.product.catalog.service.controller.ControllerHelper.supplyResponse;

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
    public ResponseEntity<Response<List<CurrencyModel>>> createCurrencies(
            @RequestBody List<CreateCurrencyDto> dtos
    ) {
        return supplyResponse(() -> currencyService.createCurrencies(dtos), log);
    }

    @GetMapping
    public ResponseEntity<Response<List<CurrencyModel>>> findCurrencies(
            @RequestParam(name = "id", required = false) List<String> ids,
            @RequestParam(name = "name", required = false) List<String> names
    ) {
        return supplyResponse(() -> currencyService.findCurrencies(ids, names), log);
    }

    @GetMapping("find-one")
    public ResponseEntity<Response<CurrencyModel>> findOneById(
            @RequestParam(name = "id") String id
    ) {
        return supplyResponse(() -> currencyService.findById(id), log);
    }
}
