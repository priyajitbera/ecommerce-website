package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.IndexProductsInElasticSearchDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.exceptionhandler.MethodArgumentNotValidExceptionHandler;
import com.priyajit.ecommerce.product.catalog.service.model.IndexProductsInElasticSearchModel;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.model.Response;
import com.priyajit.ecommerce.product.catalog.service.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.priyajit.ecommerce.product.catalog.service.controller.ControllerHelper.getUserId;
import static com.priyajit.ecommerce.product.catalog.service.controller.ControllerHelper.supplyResponse;

@Slf4j
@RestController
@RequestMapping("/v1/product")
@CrossOrigin("*")
public class ProductControllerV1 implements MethodArgumentNotValidExceptionHandler {

    private ProductService productService;

    @Autowired
    public ProductControllerV1(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Response<PaginatedProductList>> findProducts(
            @RequestParam(required = false) List<String> productIds,
            @RequestParam(required = false) String productNamePart,
            @RequestParam(required = false) List<String> produdctCategoryIds,
            @RequestParam(required = false) List<String> productCategoryNames,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        return supplyResponse(
                () -> productService.findProducts(
                        productIds, productNamePart, produdctCategoryIds, productCategoryNames,
                        pageIndex, pageSize
                ), log);
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PaginatedProductList>> search(
            @RequestParam(required = true) String searchKeyword,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        return supplyResponse(
                () -> productService.search(searchKeyword, pageIndex, pageSize), log);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Response<ProductModel>> createProduct(
            Authentication auth,
            @Valid @RequestBody CreateProductDto dto
    ) {
        return supplyResponse(() -> productService.createProduct(dto, getUserId(auth)), log);
    }

    @PatchMapping
    public ResponseEntity<Response<ProductModel>> updateProduct(
            @RequestBody UpdateProductDto dto,
            @RequestHeader("userId") String userId
    ) {
        return supplyResponse(() -> productService.updateProduct(dto, userId), log);
    }

    @PostMapping("/elastic-search/index")
    public ResponseEntity<Response<IndexProductsInElasticSearchModel>> indexProductsInElasticSearch(
            @RequestBody IndexProductsInElasticSearchDto indexProductsInElasticSearchDto
    ) {
        return supplyResponse(
                () -> productService.indexProductsInElasticSearch(indexProductsInElasticSearchDto),
                log);
    }

    @GetMapping("/find-one")
    public ResponseEntity<Response<ProductModel>> findOneById(
            @RequestParam("productId") String productId
    ) {
        return supplyResponse(() -> productService.findOneById(productId), log);
    }
}
