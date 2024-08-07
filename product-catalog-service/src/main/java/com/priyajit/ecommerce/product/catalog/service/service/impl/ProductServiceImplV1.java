package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.IndexProductsDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import com.priyajit.ecommerce.product.catalog.service.entity.*;
import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductDoc;
import com.priyajit.ecommerce.product.catalog.service.esrepository.ProductDocRepository;
import com.priyajit.ecommerce.product.catalog.service.exception.CurrencyNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductCategoryNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductImageNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.IndexedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.model.SellerProductList;
import com.priyajit.ecommerce.product.catalog.service.redisrepository.ProductModelRedisRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.ProductRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.CurrencyRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductCategoryRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductImageRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.service.ProductService;
import com.priyajit.ecommerce.user_management_service.api.UserControllerV1Api;
import com.priyajit.ecommerce.user_management_service.model.FindUserModel;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private UserControllerV1Api userControllerV1Api;

    public ProductServiceImplV1(
            ProductRepositoryQueryMethod productRepositoryQueryMethod,
            ProductRepository productRepository,
            ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod,
            ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod,
            CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod,
            ProductDocRepository productDocRepository,
            ProductModelRedisRepository productModelRedisRepository,
            UserControllerV1Api userControllerV1Api
    ) {

        this.productRepositoryQueryMethod = productRepositoryQueryMethod;
        this.productRepository = productRepository;
        this.productCategoryRepositoryQueryMethod = productCategoryRepositoryQueryMethod;
        this.productImageRepositoryQueryMethod = productImageRepositoryQueryMethod;
        this.currencyRepositoryQueryMethod = currencyRepositoryQueryMethod;
        this.productDocRepository = productDocRepository;
        this.productModelRedisRepository = productModelRedisRepository;
        this.userControllerV1Api = userControllerV1Api;
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

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SellerProductList findSellersProducts(
            String sellerUserId,
            List<String> productIds,
            String productNamePart,
            List<String> productCategoryIds,
            List<String> productCategoryNames,
            Integer pageIndex,
            Integer pageSize
    ) {
        var productsPage = productRepository.findProductsByCreatedByUserId(
                sellerUserId, productIds, productNamePart, productCategoryIds, productCategoryNames, pageIndex, pageSize);

        // fetch users by id
        var userIds = Stream.concat(
                Stream.concat(
                        productsPage.get().map(Product::getCreatedByUserId),
                        productsPage.get().map(Product::getLastModifiedByUserId)
                ),
                Stream.of(sellerUserId)
        ).filter(Objects::nonNull).distinct().toList();
        var users = findUsers(userIds);

        // create response model and return
        return SellerProductList.from(productsPage, users);
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
        product.setProductIndexingInfo(ProductIndexingInfo.builder().product(product).build());

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

    @Override
    public PaginatedProductList search(String searchKeyword, int pageIndex, int pageSize) {
        if (searchKeyword == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "searchKeyword must not be null");

        Page<ProductDoc> productDocPage = productDocRepository.search(
                searchKeyword,
                pageIndex, pageSize
        );

        return PaginatedProductList.buildFrom(productDocPage);
    }

    @Override
    public IndexedProductList indexProductsInElasticSearch(@Valid IndexProductsDto dto, String userId) {
        if (dto == null || dto.getProductIds() == null)
            throw new IllegalArgumentException("Expected non null value for IndexProductsInElasticSearchDto and product ids");

        // fetch products from main DB
        List<Product> products = productRepositoryQueryMethod.findAllById(dto.getProductIds());

        // index products
        List<String> indexProductsIds = productDocRepository.indexAll(ProductDoc.buildFrom(products));

        // update index audit info
        var now = ZonedDateTime.now();
        products.forEach(product -> {
            if (product.getProductIndexingInfo() == null)
                product.setProductIndexingInfo(ProductIndexingInfo.builder().product(product).build());
            product.getProductIndexingInfo().setElasticSearchIndexedOn(now);
            product.getProductIndexingInfo().setIsIndexedOnElasticSearch(true);
        });
        productRepositoryQueryMethod.saveAllAndFlush(products);

        return IndexedProductList.buildFrom(products);
    }

    @Override
    public IndexedProductList deIndexProductsInElasticSearch(@Valid IndexProductsDto dto, String userId) {
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
            if (product.getProductIndexingInfo() == null)
                product.setProductIndexingInfo(ProductIndexingInfo.builder().product(product).build());
            product.getProductIndexingInfo().setElasticSearchIndexedOn(null);
            product.getProductIndexingInfo().setIsIndexedOnElasticSearch(false);
            product.getProductIndexingInfo().setIndexedByUserId(userId);
        });
        productRepositoryQueryMethod.saveAllAndFlush(products);

        return IndexedProductList.buildFrom(products);
    }

    @Override
    public ProductModel findOneById(String productId) {
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

    private Map<String, FindUserModel> findUsers(List<String> userIds) {
        return userControllerV1Api.findUsers(userIds.stream().map(Integer::valueOf).toList())
                .stream()
                .collect(() -> new HashMap<>(), (map, user) -> {
                    map.put(String.valueOf(user.getId()), user);
                }, (t1, t2) -> {
                });
    }
}
