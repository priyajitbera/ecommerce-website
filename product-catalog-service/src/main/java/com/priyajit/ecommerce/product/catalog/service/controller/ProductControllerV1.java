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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        try {
            var model = productService.findProducts(
                    productIds, productNamePart, produdctCategoryIds, productCategoryNames,
                    pageIndex, pageSize
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<PaginatedProductList>builder()
                            .data(model)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<PaginatedProductList>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<PaginatedProductList>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PaginatedProductList>> search(
            @RequestParam(required = true) String searchKeyword,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        try {
            var model = productService.search(searchKeyword, pageIndex, pageSize);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<PaginatedProductList>builder()
                            .data(model)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<PaginatedProductList>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<PaginatedProductList>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<Response<ProductModel>> createProduct(
            @Valid @RequestBody CreateProductDto dto,
            @RequestHeader(value = "userId") String userId
    ) {
        try {
            var models = productService.createProduct(dto, userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<ProductModel>builder()
                            .data(models)
                            .build());
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<ProductModel>builder()
                            .error(e.getReason())
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<ProductModel>builder().build());
        }
    }

    @PatchMapping
    public ResponseEntity<Response<ProductModel>> updateProduct(
            @RequestBody UpdateProductDto dto,
            @RequestHeader("userId") String userId
    ) {
        try {
            var model = productService.updateProduct(dto, userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<ProductModel>builder()
                            .data(model)
                            .build());
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<ProductModel>builder()
                            .error(e.getReason())
                            .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<ProductModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/elastic-search/index")
    public ResponseEntity<Response<IndexProductsInElasticSearchModel>> indexProductsInElasticSearch(
            @RequestBody IndexProductsInElasticSearchDto indexProductsInElasticSearchDto
    ) {
        try {
            var model = productService.indexProductsInElasticSearch(indexProductsInElasticSearchDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<IndexProductsInElasticSearchModel>builder()
                            .data(model)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<IndexProductsInElasticSearchModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<IndexProductsInElasticSearchModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Response<ProductModel>> getProduct(
            @RequestParam("productId") String productId
    ) {
        try {
            var model = productService.getProduct(productId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<ProductModel>builder()
                            .data(model)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<ProductModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<ProductModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }


}
