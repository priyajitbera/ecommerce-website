package com.priyajit.product.ecommerce.catalog.service.service.impl;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.DeleteProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.UpdateProductDto;
import com.priyajit.product.ecommerce.catalog.service.entity.*;
import com.priyajit.product.ecommerce.catalog.service.exception.CurrencyNotFoundException;
import com.priyajit.product.ecommerce.catalog.service.exception.ProductCategoryNotFoundException;
import com.priyajit.product.ecommerce.catalog.service.exception.ProductImageNotFoundException;
import com.priyajit.product.ecommerce.catalog.service.exception.ProductNotFoundException;
import com.priyajit.product.ecommerce.catalog.service.model.PaginatedProductList;
import com.priyajit.product.ecommerce.catalog.service.model.ProductModel;
import com.priyajit.product.ecommerce.catalog.service.repository.CurrencyRepository;
import com.priyajit.product.ecommerce.catalog.service.repository.ProductCategoryRepository;
import com.priyajit.product.ecommerce.catalog.service.repository.ProductImageRepository;
import com.priyajit.product.ecommerce.catalog.service.repository.ProductRepository;
import com.priyajit.product.ecommerce.catalog.service.service.ProductService;
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

    private ProductRepository productRepository;
    private ProductCategoryRepository productCategoryRepository;
    private ProductImageRepository productImageRepository;
    private CurrencyRepository currencyRepository;

    public ProductServiceImplV1(
            ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository,
            ProductImageRepository productImageRepository,
            CurrencyRepository currencyRepository
    ) {

        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productImageRepository = productImageRepository;
        this.currencyRepository = currencyRepository;
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
    public PaginatedProductList findProducts(List<String> productIds, int pageIndex, int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);

        Page<Product> productPage = productRepository.findByIdIn(productIds, pageRequest);

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

        List<Product> products = productRepository.saveAllAndFlush(productList);

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

        Currency currency = currencyRepository.findById(currencyId)
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

        return productImageIds.stream().map(imageId -> productImageRepository.findById(imageId)
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
                .map(categoryId -> productCategoryRepository.findById(categoryId)
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
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(ProductNotFoundException.supplier(dto.getProductId()));

            updateProductFromDto(product, dto);
            return product;
        }).collect(Collectors.toList());

        List<Product> savedProducts = productRepository.saveAllAndFlush(updatedProducts);

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
            Currency currency = currencyRepository.findById(dto.getCurrencyId())
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

        List<Product> toDelete = dtos.stream().map(dto -> productRepository.findById(dto.getProductId())
                        .orElseThrow(ProductNotFoundException.supplier(dto.getProductId())))
                .collect(Collectors.toList());

        productRepository.deleteAll(toDelete);

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
