package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.IndexProductsInElasticSearchDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.model.IndexProductsInElasticSearchModel;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/product")
@CrossOrigin("*")
public class ProductControllerV1 {

    private ProductService productService;

    @Autowired
    public ProductControllerV1(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PaginatedProductList> findProducts(
            @RequestParam(required = false) List<String> productIds,
            @RequestParam(required = false) String productNamePart,
            @RequestParam(required = false) List<String> produdctCategoryIds,
            @RequestParam(required = false) List<String> productCategoryNames,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        return ok(productService.findProducts(
                productIds, productNamePart, produdctCategoryIds, productCategoryNames,
                pageIndex, pageSize));
    }

    @GetMapping("/sellers")
    public ResponseEntity<PaginatedProductList> findSellersProducts(
            @RequestHeader(name = "userId") String userId,
            @RequestParam(required = false) List<String> productIds,
            @RequestParam(required = false) String productNamePart,
            @RequestParam(required = false) List<String> produdctCategoryIds,
            @RequestParam(required = false) List<String> productCategoryNames,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        return ok(productService.findSellersProducts(
                userId,
                productIds, productNamePart, produdctCategoryIds, productCategoryNames,
                pageIndex, pageSize));
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedProductList> search(
            @RequestParam(required = true) String searchKeyword,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        return ok(productService.search(searchKeyword, pageIndex, pageSize));
    }

    @PostMapping
    public ResponseEntity<ProductModel> createProduct(
            @Valid @RequestBody CreateProductDto dto,
            @RequestHeader(name = "userId") String userId
    ) {
        return ok(productService.createProduct(dto, userId));
    }

    @PatchMapping
    public ResponseEntity<ProductModel> updateProduct(
            @RequestBody UpdateProductDto dto,
            @RequestHeader(name = "userId") String userId
    ) {
        return ok(productService.updateProduct(dto, userId));
    }

    @PostMapping("/elastic-search/index")
    public ResponseEntity<IndexProductsInElasticSearchModel> indexProductsInElasticSearch(
            @RequestBody IndexProductsInElasticSearchDto indexProductsInElasticSearchDto
    ) {
        return ok(productService.indexProductsInElasticSearch(indexProductsInElasticSearchDto));
    }

    @GetMapping("/find-one")
    public ResponseEntity<ProductModel> findOneById(
            @RequestParam("productId") String productId
    ) {
        return ok(productService.findOneById(productId));
    }
}
