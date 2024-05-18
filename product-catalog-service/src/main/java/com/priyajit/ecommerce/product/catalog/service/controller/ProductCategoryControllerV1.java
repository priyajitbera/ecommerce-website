package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductCategoryDto;
import com.priyajit.ecommerce.product.catalog.service.model.ProductCategoryModel;
import com.priyajit.ecommerce.product.catalog.service.model.Response;
import com.priyajit.ecommerce.product.catalog.service.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/product-category")
@CrossOrigin("*")
public class ProductCategoryControllerV1 {

    private ProductCategoryService productCategoryService;

    public ProductCategoryControllerV1(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping
    public ResponseEntity<Response<List<ProductCategoryModel>>> createProductCategories(
            @RequestBody List<CreateProductCategoryDto> dtos) {

        try {
            var models = productCategoryService.createProductCategories(dtos);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<List<ProductCategoryModel>>builder()
                            .data(models)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<List<ProductCategoryModel>>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<List<ProductCategoryModel>>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<Response<List<ProductCategoryModel>>> findProductCategories(
            @RequestParam(name = "id", required = false) List<String> ids,
            @RequestParam(name = "name", required = false) List<String> names
    ) {
        try {
            var models = productCategoryService.findProductCategories(ids, names);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<List<ProductCategoryModel>>builder()
                            .data(models)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<List<ProductCategoryModel>>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<List<ProductCategoryModel>>builder()
                            .error(e.getMessage())
                            .build());
        }
    }
}
