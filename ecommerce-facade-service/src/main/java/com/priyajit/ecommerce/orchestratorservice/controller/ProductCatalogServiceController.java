package com.priyajit.ecommerce.orchestratorservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.priyajit.ecommerce.orchestrator_service.api.ProductCatalogServiceApi;
import com.priyajit.ecommerce.orchestrator_service.model.CreateProductDto;
import com.priyajit.ecommerce.orchestrator_service.model.PaginatedProductList;
import com.priyajit.ecommerce.orchestrator_service.model.ProductModel;
import com.priyajit.ecommerce.orchestratorservice.component.CustomObjectMapper;
import com.priyajit.ecommerce.orchestratorservice.component.SecurityContextHelper;
import com.priyajit.ecommerce.product_catalog_service.api.CurrencyControllerV1Api;
import com.priyajit.ecommerce.product_catalog_service.api.ProductCategoryControllerV1Api;
import com.priyajit.ecommerce.product_catalog_service.api.ProductControllerV1Api;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin("*")
public class ProductCatalogServiceController implements ProductCatalogServiceApi {

    private ProductControllerV1Api productControllerV1Api;
    private CurrencyControllerV1Api currencyControllerV1Api;
    private ProductCategoryControllerV1Api productCategoryControllerV1Api;
    private CustomObjectMapper objectMapper;
    private SecurityContextHelper securityContextHelper;

    public ProductCatalogServiceController(
            ProductControllerV1Api productControllerV1Api,
            CurrencyControllerV1Api currencyControllerV1Api,
            ProductCategoryControllerV1Api productCategoryControllerV1Api,
            CustomObjectMapper objectMapper,
            SecurityContextHelper securityContextHelper
    ) {
        this.productControllerV1Api = productControllerV1Api;
        this.currencyControllerV1Api = currencyControllerV1Api;
        this.productCategoryControllerV1Api = productCategoryControllerV1Api;
        this.objectMapper = objectMapper;
        this.securityContextHelper = securityContextHelper;
    }


    /**
     * POST /product-catalog-service/v1/product
     *
     * @param userToken        (required)
     * @param createProductDto (required)
     * @return OK (status code 200)
     * or Bad Request (status code 400)
     */
    @Override
    @PreAuthorize("hasAuthority('SELLER')")
    public Mono<ResponseEntity<ProductModel>> createProduct(
            String userToken,
            @Valid @RequestBody Mono<CreateProductDto> createProductDto,
            final ServerWebExchange exchange
    ) throws Exception {

        return Mono.zip(securityContextHelper.getUserId(), createProductDto)
                .doFirst(() -> log.info("Before calling productControllerV1Api.createProduct"))
                .flatMap(tuple -> {
                    var payLoad = objectMapper.map(tuple.getT2(), com.priyajit.ecommerce.product_catalog_service.model.CreateProductDto.class);
                    return productControllerV1Api.createProductWithHttpInfo(tuple.getT1(), payLoad);
                })
                .doOnSuccess((model) -> log.info("After calling productControllerV1Api.createProduct"))
                .doOnError((e) -> log.info("After calling productControllerV1Api.createProduct error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(objectMapper.map(response.getBody(), ProductModel.class)));
    }

    /**
     * GET /product-catalog-service/v1/product/search
     *
     * @param searchKeyword (required)
     * @param pageIndex     (optional, default to 0)
     * @param pageSize      (optional, default to 10)
     * @return OK (status code 200)
     * or Bad Request (status code 400)
     */
    public Mono<ResponseEntity<PaginatedProductList>> search(
            @Valid String searchKeyword,
            @Valid Optional<Integer> pageIndex,
            @Valid Optional<Integer> pageSize,
            final ServerWebExchange exchange
    ) throws Exception {

        return Mono.empty().doFirst(() -> log.info("[search] reqId: {}", exchange.getRequest().getId()))
                .then(productControllerV1Api.searchWithHttpInfo(searchKeyword, pageIndex.orElse(null), pageSize.orElse(null)))
                .doOnSuccess((model) -> log.info("After calling productControllerV1Api.searchWithHttpInfo"))
                .doOnError((e) -> log.info("After calling productControllerV1Api.searchWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(objectMapper.map(response.getBody(), PaginatedProductList.class)));

    }

    /**
     * GET /product-catalog-service/v1/currency
     *
     * @param id   (optional)
     * @param name (optional)
     * @return OK (status code 200)
     */
    @Override
    public Mono<ResponseEntity<Flux<com.priyajit.ecommerce.orchestrator_service.model.CurrencyModel>>> findCurrencies(
            @Valid Optional<List<String>> id,
            @Valid Optional<List<String>> name,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) throws Exception {

        return Mono.empty().doFirst(() -> log.info("[findCurrencies] reqId: {}", exchange.getRequest().getId()))
                .then(currencyControllerV1Api.findCurrenciesWithHttpInfo(id.orElse(null), name.orElse(null)))
                .doOnSuccess((model) -> log.info("After calling currencyControllerV1Api.findCurrenciesWithHttpInfo"))
                .doOnError((e) -> log.info("After calling currencyControllerV1Api.findCurrenciesWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .body(Flux.fromIterable(objectMapper.map(response.getBody(), new TypeReference<>() {
                        }))));


    }

    /**
     * GET /product-catalog-service/v1/product-category
     *
     * @param id   (optional)
     * @param name (optional)
     * @return OK (status code 200)
     */
    @Override
    public Mono<ResponseEntity<Flux<com.priyajit.ecommerce.orchestrator_service.model.ProductCategoryModel>>> findProductCategories(
            @Valid Optional<List<String>> id,
            @Valid @RequestParam(value = "name", required = false) Optional<List<String>> name,
            final ServerWebExchange exchange
    ) throws Exception {
        return Mono.empty().doFirst(() -> log.info("[findProductCategories] reqId: {}", exchange.getRequest().getId()))
                .then(productCategoryControllerV1Api.findProductCategoriesWithHttpInfo(id.orElse(null), name.orElse(null)))
                .doOnSuccess((model) -> log.info("After calling productCategoryControllerV1Api.findProductCategoriesWithHttpInfo"))
                .doOnError((e) -> log.info("After calling productCategoryControllerV1Api.findProductCategoriesWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .body(Flux.fromIterable(objectMapper.map(response.getBody(), new TypeReference<>() {
                        }))));
    }
}