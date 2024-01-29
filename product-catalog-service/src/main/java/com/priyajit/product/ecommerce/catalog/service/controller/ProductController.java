package com.priyajit.product.ecommerce.catalog.service.controller;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.DeleteProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.UpdateProductDto;
import com.priyajit.product.ecommerce.catalog.service.model.PaginatedProductList;
import com.priyajit.product.ecommerce.catalog.service.model.ProductModel;
import com.priyajit.product.ecommerce.catalog.service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product/v1")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    PaginatedProductList findProducts(
            @RequestParam List<String> productIds,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) throws InterruptedException {
        return productService.findProducts(productIds, pageIndex, pageSize);
    }

    @PostMapping
    List<ProductModel> createProducts(
            @RequestBody List<CreateProductDto> dtos
    ) {
        return productService.createProducts(dtos);
    }

    @PatchMapping
    List<ProductModel> updateProducts(
            @RequestBody List<UpdateProductDto> dtos
    ) {
        return productService.updateProducts(dtos);
    }

    @DeleteMapping
    List<ProductModel> deleteProduct(
            @RequestBody List<DeleteProductDto> dtos
    ) {
        return productService.deleteProducts(dtos);
    }
}
