package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateCurrencyDto;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import com.priyajit.ecommerce.product.catalog.service.model.Response;
import com.priyajit.ecommerce.product.catalog.service.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public List<CurrencyModel> createCurrencies(
            @RequestBody List<CreateCurrencyDto> dtos
    ) {
        return currencyService.createCurrencies(dtos);
    }

    @GetMapping
    public ResponseEntity<Response<List<CurrencyModel>>> findCurrencies(
            @RequestParam(name = "id", required = false) List<String> ids,
            @RequestParam(name = "name", required = false) List<String> names
    ) {
        try {
            var models = currencyService.findCurrencies(ids, names);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<List<CurrencyModel>>builder()
                            .data(models)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<List<CurrencyModel>>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<List<CurrencyModel>>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @GetMapping("find-one")
    public ResponseEntity<Response<CurrencyModel>> findOneById(
            @RequestParam(name = "id") String id
    ) {
        try {
            var model = currencyService.findById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<CurrencyModel>builder()
                            .data(model)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<CurrencyModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<CurrencyModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }
}
