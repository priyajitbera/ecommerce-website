package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductCategoryDto;
import com.priyajit.ecommerce.product.catalog.service.model.ProductCategoryModel;
import com.priyajit.ecommerce.product.catalog.service.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

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
    public ResponseEntity<List<ProductCategoryModel>> createProductCategories(
            @RequestBody List<CreateProductCategoryDto> dtos
    ) {
        return ok(productCategoryService.createProductCategories(dtos));
    }

    @GetMapping
    public ResponseEntity<List<ProductCategoryModel>> findProductCategories(
            @RequestParam(name = "id", required = false) List<String> ids,
            @RequestParam(name = "name", required = false) List<String> names
    ) {
        return ok(productCategoryService.findProductCategories(ids, names));
    }
}
