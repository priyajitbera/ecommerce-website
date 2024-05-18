package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.DeleteProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.IndexProductsInElasticSearchDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.entity.*;
import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductDoc;
import com.priyajit.ecommerce.product.catalog.service.esrepository.ProductDocRepository;
import com.priyajit.ecommerce.product.catalog.service.exception.CurrencyNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductCategoryNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductImageNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.DeleteProductsInElasticSearchModel;
import com.priyajit.ecommerce.product.catalog.service.model.IndexProductsInElasticSearchModel;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.redisrepository.ProductModelRedisRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.ProductRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.CurrencyRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductCategoryRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductImageRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImplV1 implements ProductService {

    private ProductRepositoryQueryMethod productRepositoryQueryMethod;
    private ProductRepository productRepository;
    private ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod;
    private ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod;
    private CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod;
    private ProductDocRepository productDocRepository;
    private ProductModelRedisRepository productModelRedisRepository;

    public ProductServiceImplV1(
            ProductRepositoryQueryMethod productRepositoryQueryMethod,
            ProductRepository productRepository,
            ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod,
            ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod,
            CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod,
            ProductDocRepository productDocRepository,
            ProductModelRedisRepository productModelRedisRepository
    ) {

        this.productRepositoryQueryMethod = productRepositoryQueryMethod;
        this.productRepository = productRepository;
        this.productCategoryRepositoryQueryMethod = productCategoryRepositoryQueryMethod;
        this.productImageRepositoryQueryMethod = productImageRepositoryQueryMethod;
        this.currencyRepositoryQueryMethod = currencyRepositoryQueryMethod;
        this.productDocRepository = productDocRepository;
        this.productModelRedisRepository = productModelRedisRepository;
    }

    /**
     * Method to search product with productIds with pagination
     *
     * @param productIds
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PaginatedProductList findProducts(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex,
            int pageSize
    ) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);

        Page<Product> productPage = productRepository.findProducts(
                productIds,
                productNamePart,
                productCategoryIds,
                productCategoryNames,
                pageIndex,
                pageSize
        );

        // create response model and return
        return PaginatedProductList.from(productPage);
    }

    /**
     * Method to create Products
     *
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProductModel createProduct(@Valid CreateProductDto dto, String userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId can not be null");
        var product = buildProductFromDto(dto);
        product.setCreatedByUserId(userId);
        productRepositoryQueryMethod.saveAndFlush(product);
        return ProductModel.from(product);
    }

    /**
     * Method to build a Product object from dto
     *
     * @param dto
     * @return
     */
    private Product buildProductFromDto(CreateProductDto dto) {

        SortedSet<ProductCategory> taggedCategories = fetchProductCategories(dto.getTaggedCategoryIds())
                .stream().collect(Collectors.toCollection(() -> new TreeSet<>()));
        SortedSet<ProductImage> productImages = fetchProductImages(dto.getProductImageIds())
                .stream().collect(Collectors.toCollection(() -> new TreeSet<>()));
        ProductPrice productPrice = buildProductPrice(dto.getPrice(), dto.getCurrencyId());

        var product = Product.builder()
                .title(dto.getTitle())
                .price(productPrice)
                .description(dto.getDescription())
                .taggedCategories(taggedCategories)
                .images(productImages)
                .build();

        productPrice.setProduct(product);
        productImages.forEach(image -> image.setProduct(product));
        taggedCategories.forEach(category -> category.getTaggedProducts().add(product));

        return product;
    }

    /**
     * Helper method to build a ProductPrice object
     *
     * @param price
     * @param currencyId
     * @return
     */
    private ProductPrice buildProductPrice(BigDecimal price, String currencyId) {

        Currency currency = currencyRepositoryQueryMethod.findById(currencyId)
                .orElseThrow(CurrencyNotFoundException.supplier(currencyId));

        return ProductPrice.builder()
                .price(price)
                .currency(currency)
                .build();
    }

    /**
     * Helper method to fetch ProductImages by productImageIds
     *
     * @param productImageIds
     * @return
     */
    private List<ProductImage> fetchProductImages(List<String> productImageIds) {
        if (productImageIds == null) return List.of(); // empty list

        return productImageIds.stream().map(imageId -> productImageRepositoryQueryMethod.findById(imageId)
                        .orElseThrow(ProductImageNotFoundException.supplier(imageId)))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to fetch ProductCategories by productCategoryIds
     *
     * @param categoryIds
     * @return
     */
    private List<ProductCategory> fetchProductCategories(
            @Nullable List<String> categoryIds
    ) {
        if (categoryIds == null) return List.of(); // empty list

        return categoryIds.stream()
                .map(categoryId -> productCategoryRepositoryQueryMethod.findById(categoryId)
                        .orElseThrow(ProductCategoryNotFoundException.supplier((categoryId))))
                .collect(Collectors.toList());
    }

    /**
     * Method to update Products
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProductModel updateProduct(@Valid UpdateProductDto dto, String userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must not be null");

        var product = productRepositoryQueryMethod.findById(dto.getProductId())
                .orElseThrow(ProductNotFoundException.supplier(dto.getProductId()));

        updateProductFromDto(product, dto);
        product.setLastModifiedByUserId(userId);

        productRepositoryQueryMethod.saveAndFlush(product);

        // create response model and return
        return ProductModel.from(product);
    }

    /**
     * Helper method to update Product entity with data from UpdateProductDto
     *
     * @param product
     * @param dto
     */
    private void updateProductFromDto(Product product, UpdateProductDto dto) {

        // update title
        if (dto.getTitle() != null)
            product.setTitle(dto.getTitle());

        // update description
        if (dto.getDescription() != null)
            product.setDescription(dto.getDescription());

        // update price
        if (dto.getPrice() != null) {
            if (product.getPrice() == null) {
                product.setPrice(ProductPrice.builder().product(product).build());
            }
            ProductPrice productPrice = product.getPrice();
            productPrice.setPrice(dto.getPrice());
        }

        // update price currency
        if (dto.getCurrencyId() != null) {
            if (product.getPrice() == null) {
                product.setPrice(ProductPrice.builder().product(product).build());
            }
            ProductPrice productPrice = product.getPrice();
            Currency currency = currencyRepositoryQueryMethod.findById(dto.getCurrencyId())
                    .orElseThrow(CurrencyNotFoundException.supplier(dto.getCurrencyId()));
            productPrice.setCurrency(currency);
        }

        // update product image
        if (dto.getProductImageIds() != null) {
            if (product.getImages() == null) {
                product.setImages(new TreeSet<>());
            }
            SortedSet<ProductImage> productImages = product.getImages();
            productImages.clear();
            productImages.addAll(fetchProductImages(dto.getProductImageIds()));
        }

        // update taggedProductCategories
        if (dto.getTaggedCategoryIds() != null) {
            if (product.getTaggedCategories() == null) {
                product.setTaggedCategories(new TreeSet<>());
            }
            SortedSet<ProductCategory> productCategories = product.getTaggedCategories();
            productCategories.clear();
            productCategories.addAll(fetchProductCategories(dto.getTaggedCategoryIds()));
        }
    }

    /**
     * Method to delete Products
     *
     * @param dtos
     * @return
     */
    @Override
    @Transactional
    public List<ProductModel> deleteProducts(List<DeleteProductDto> dtos) {

        List<Product> toDelete = dtos.stream().map(dto -> productRepositoryQueryMethod.findById(dto.getProductId())
                        .orElseThrow(ProductNotFoundException.supplier(dto.getProductId())))
                .collect(Collectors.toList());

        productRepositoryQueryMethod.deleteAll(toDelete);

        return fromProducts(toDelete);
    }

    @Override
    public PaginatedProductList search(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable String productDescriptionPart,
            @Nullable List<String> produdctCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex, int pageSize
    ) {

        Page<ProductDoc> productDocPage = productDocRepository.search(
                productIds,
                productNamePart,
                productDescriptionPart,
                produdctCategoryIds,
                productCategoryNames,
                pageIndex, pageSize
        );

        return PaginatedProductList.buildFrom(productDocPage);
    }

    @Override
    public IndexProductsInElasticSearchModel indexProductsInElasticSearch(IndexProductsInElasticSearchDto dto) {
        if (dto == null || dto.getProductIds() == null)
            throw new IllegalArgumentException("Expected non null value for IndexProductsInElasticSearchDto and product ids");

        // fetch products from main DB
        List<Product> products = productRepositoryQueryMethod.findAllById(dto.getProductIds());

        // index products
        List<String> indexProductsIds = productDocRepository.indexAll(ProductDoc.buildFrom(products));

        // update index audit info
        products.forEach(product -> {
            product.setElasticSearchIndexedOn(ZonedDateTime.now());
            product.setIsIndexedOnElasticSearch(true);
        });
        productRepositoryQueryMethod.saveAllAndFlush(products);

        return IndexProductsInElasticSearchModel.builder()
                .productIds(indexProductsIds)
                .build();
    }

    @Override
    public DeleteProductsInElasticSearchModel deleteProductsInElasticSearch(IndexProductsInElasticSearchDto dto) {
        if (dto == null || dto.getProductIds() == null)
            throw new IllegalArgumentException("Expected non null value for IndexProductsInElasticSearchDto and product ids");

        // fetch products from main DB
        List<Product> products = productRepositoryQueryMethod.findAllById(dto.getProductIds());

        List<String> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // index products
        List<String> indexProductsIds = productDocRepository.deleteAll(productIds);

        // update index audit info
        products.forEach(product -> {
            product.setElasticSearchIndexedOn(null);
            product.setIsIndexedOnElasticSearch(false);
        });
        productRepositoryQueryMethod.saveAllAndFlush(products);

        return DeleteProductsInElasticSearchModel.builder()
                .productIds(indexProductsIds)
                .build();
    }

    @Override
    public ProductModel getProduct(String productId) {
        // first search in Redis, if found then return
        try {
            var productModelOpt = productModelRedisRepository.findById(productId);
            if (productModelOpt.isPresent()) {
                return productModelOpt.get();
            }
        } catch (Exception e) {
            log.error("Error fetching ProductModel object from Redis", e.getMessage());
            e.printStackTrace();
        }

        // search in primary DB
        var product = productRepositoryQueryMethod.findById(productId)
                .orElseThrow(ProductNotFoundException.supplier(productId));

        // create response model & save to Redis
        var productModel = ProductModel.from(product);
        saveToRedisOptimistic(productModel);

        return productModel;
    }

    private void saveToRedisOptimistic(ProductModel productModel) {
        try {
            CompletableFuture.supplyAsync(() -> productModelRedisRepository.save(productModel))
                    .thenAccept(saved -> log.info("Saved ProductModel object to Redis"))
                    .exceptionally(e -> {
                        log.error("Error while saving ProductModel object in Redis", e.getMessage());
                        e.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            log.error("Error while saving ProductModel object in Redis", e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Helper method to create response models List<ProductModel> from List<Product> products
     *
     * @param products
     * @return
     */
    private List<ProductModel> fromProducts(List<Product> products) {

        return products.stream()
                .map(ProductModel::from)
                .collect(Collectors.toList());
    }
}
