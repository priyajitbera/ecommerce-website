package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.service.ProductCategoryService;
import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductCategoryDto;
import com.priyajit.ecommerce.product.catalog.service.model.ProductCategoryModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product-category/v1")
public class ProductCategoryController {

    private ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping
    public List<ProductCategoryModel> createProductCategories(
            @RequestBody List<CreateProductCategoryDto> dtos) {

        return productCategoryService.createProductCategories(dtos);
    }

    @GetMapping
    public List<ProductCategoryModel> findProductCategories(
            @RequestParam(name = "id", required = false) List<String> ids,
            @RequestParam(name = "name", required = false) List<String> names
    ) {
        return productCategoryService.findProductCategories(ids, names);
    }
}
