package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.DeleteProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.entity.*;
import com.priyajit.ecommerce.product.catalog.service.exception.CurrencyNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductCategoryNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductImageNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.ProductRepositoryQueryDsl;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.CurrencyRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductCategoryRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductImageRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplV1 implements ProductService {

    private ProductRepositoryQueryMethod productRepositoryQueryMethod;
    private ProductRepositoryQueryDsl productRepositoryQueryDsl;
    private ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod;
    private ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod;
    private CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod;

    public ProductServiceImplV1(
            ProductRepositoryQueryMethod productRepositoryQueryMethod,
            ProductRepositoryQueryDsl productRepositoryQueryDsl,
            ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod,
            ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod,
            CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod
    ) {

        this.productRepositoryQueryMethod = productRepositoryQueryMethod;
        this.productRepositoryQueryDsl = productRepositoryQueryDsl;
        this.productCategoryRepositoryQueryMethod = productCategoryRepositoryQueryMethod;
        this.productImageRepositoryQueryMethod = productImageRepositoryQueryMethod;
        this.currencyRepositoryQueryMethod = currencyRepositoryQueryMethod;
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

        Page<Product> productPage = productRepositoryQueryDsl.findProducts(
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
     * @param dtoList
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ProductModel> createProducts(List<CreateProductDto> dtoList) {

        List<Product> productList = dtoList.stream()
                .map(this::buildProductFromDto)
                .collect(Collectors.toList());

        List<Product> products = productRepositoryQueryMethod.saveAllAndFlush(productList);

        // create response models and return
        return fromProducts(products);
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

        return Product.builder()
                .title(dto.getTitle())
                .price(productPrice)
                .description(dto.getDescription())
                .taggedCategories(taggedCategories)
                .images(productImages)
                .build();
    }

    /**
     * Helper method to build a ProductPrice object
     *
     * @param price
     * @param currencyId
     * @return
     */
    private ProductPrice buildProductPrice(long price, String currencyId) {

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
     * @param dtos
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ProductModel> updateProducts(List<UpdateProductDto> dtos) {

        List<Product> updatedProducts = dtos.stream().map(dto -> {
            Product product = productRepositoryQueryMethod.findById(dto.getProductId())
                    .orElseThrow(ProductNotFoundException.supplier(dto.getProductId()));

            updateProductFromDto(product, dto);
            return product;
        }).collect(Collectors.toList());

        List<Product> savedProducts = productRepositoryQueryMethod.saveAllAndFlush(updatedProducts);

        // create response model and return
        return savedProducts.stream()
                .map(ProductModel::from)
                .collect(Collectors.toList());
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
