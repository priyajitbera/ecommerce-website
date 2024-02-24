package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.DeleteProductDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductImage;
import com.priyajit.ecommerce.product.catalog.service.exception.CurrencyNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductCategoryNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductImageNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.repository.CurrencyRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.ProductCategoryRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.ProductImageRepository;
import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import com.priyajit.ecommerce.product.catalog.service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplV1Test {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private ProductServiceImplV1 productService;

    @Test
    void createProducts_success() {

        // arrange
        String productCategoryId1 = "PC-1";
        String productCategoryId2 = "PC-2";
        String productImageId1 = "PI-1";
        String productImageId2 = "PI-1";
        String currencyId1 = "CUR-1";
        String currencyId2 = "CUR-2";

        List<CreateProductDto> dtos = List.of(
                CreateProductDto.builder()
                        .title("Apple iPhone 15 Plus (256 GB) - Blue")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build(),

                CreateProductDto.builder()
                        .title("Apple 2023 MacBook Air Laptop with M2 chip")
                        .description("38.91 cm (15.3 inch) Liquid Retina Display, 8GB RAM 512GB SSD Storage.")
                        .currencyId(currencyId2)
                        .price(15490000L)
                        .productImageIds(List.of(productImageId2))
                        .taggedCategoryIds(List.of(productCategoryId2))
                        .build()
        );
        ProductCategory productCategory1 = ProductCategory.builder().id(productCategoryId1).build();
        ProductCategory productCategory2 = ProductCategory.builder().id(productCategoryId2).build();
        ProductImage productImage1 = ProductImage.builder().id(productImageId1).build();
        ProductImage productImage2 = ProductImage.builder().id(productImageId2).build();
        Currency currency1 = Currency.builder().id(currencyId1).build();
        Currency currency2 = Currency.builder().id(currencyId2).build();

        // mock method calls
        when(productRepository.saveAllAndFlush(
                Mockito.anyList())).then(i -> mockSave(i.getArgument(0, List.class)));
        when(productCategoryRepository.findById(productCategoryId1))
                .thenReturn(Optional.of(productCategory1));
        when(productCategoryRepository.findById(productCategoryId2))
                .thenReturn(Optional.of(productCategory2));
        when(productImageRepository.findById(productImageId1))
                .thenReturn(Optional.of(productImage1));
        when(productImageRepository.findById(productImageId2))
                .thenReturn(Optional.of(productImage2));
        when(currencyRepository.findById(currencyId1))
                .thenReturn(Optional.of(currency1));
        when(currencyRepository.findById(currencyId2))
                .thenReturn(Optional.of(currency2));


        // act
        List<ProductModel> productModels = productService.createProducts(dtos);

        // assert
        assertNotNull(productModels);

        assertEquals(dtos.size(), productModels.size());

        assertNotNull(productModels.get(0).getId());
        assertNotNull(productModels.get(1).getId());

        assertNotNull(productModels.get(0).getCreatedOn());
        assertNotNull(productModels.get(1).getCreatedOn());

        assertNotNull(productModels.get(0).getLastModifiedOn());
        assertNotNull(productModels.get(1).getLastModifiedOn());

        assertEquals(dtos.get(0).getTitle(), productModels.get(0).getTitle());
        assertEquals(dtos.get(1).getTitle(), productModels.get(1).getTitle());

        assertEquals(dtos.get(0).getDescription(), productModels.get(0).getDescription());
        assertEquals(dtos.get(1).getDescription(), productModels.get(1).getDescription());

        assertEquals(dtos.get(0).getPrice(), productModels.get(0).getPrice().getPrice());
        assertEquals(dtos.get(1).getPrice(), productModels.get(1).getPrice().getPrice());

        assertEquals(dtos.get(0).getTaggedCategoryIds().get(0), productModels.get(0).getTaggedCategories().get(0).getId());
        assertEquals(dtos.get(1).getTaggedCategoryIds().get(0), productModels.get(1).getTaggedCategories().get(0).getId());
    }

    @Test
    void createProducts_invalidProductCategoryId_fail() {

        // arrange
        String productCategoryId1 = "PC-NOT-PRESENT";
        String productImageId1 = "PI-1";
        String currencyId1 = "CUR-1";

        List<CreateProductDto> dtos = List.of(
                CreateProductDto.builder()
                        .title("Apple iPhone 15 Plus (256 GB) - Blue")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build()
        );
        ProductImage productImage1 = ProductImage.builder().id(productImageId1).build();
        Currency currency1 = Currency.builder().id(currencyId1).build();

        // mock method calls
        when(productCategoryRepository.findById(productCategoryId1))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductCategoryNotFoundException.class, () -> productService.createProducts(dtos));
    }

    @Test
    void createProducts_invalidProductImageId_fail() {

        // arrange
        String productCategoryId1 = "PC-NOT-PRESENT";
        String productImageId1 = "PI-NOT-PRESENT";
        String currencyId1 = "CUR-1";

        List<CreateProductDto> dtos = List.of(
                CreateProductDto.builder()
                        .title("Apple iPhone 15 Plus (256 GB) - Blue")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build()
        );

        ProductCategory productCategory1 = ProductCategory.builder().id(productCategoryId1).build();
        Currency currency1 = Currency.builder().id(currencyId1).build();

        // mock method calls
        when(productCategoryRepository.findById(productCategoryId1))
                .thenReturn(Optional.of(productCategory1));
        when(productImageRepository.findById(productImageId1))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductImageNotFoundException.class, () -> productService.createProducts(dtos));
    }


    @Test
    void createProducts_invalidCurrencyId_fail() {

        // arrange
        String productCategoryId1 = "PC-NOT-PRESENT";
        String productImageId1 = "PI-NOT-PRESENT";
        String currencyId1 = "CUR-NOT-PRESENT";

        List<CreateProductDto> dtos = List.of(
                CreateProductDto.builder()
                        .title("Apple iPhone 15 Plus (256 GB) - Blue")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build()
        );

        ProductCategory productCategory1 = ProductCategory.builder().id(productCategoryId1).build();
        ProductImage productImage1 = ProductImage.builder().id(productImageId1).build();

        // mock method calls
        when(productCategoryRepository.findById(productCategoryId1))
                .thenReturn(Optional.of(productCategory1));
        when(productImageRepository.findById(productImageId1))
                .thenReturn(Optional.of(productImage1));
        when(currencyRepository.findById(currencyId1))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(CurrencyNotFoundException.class, () -> productService.createProducts(dtos));
    }


    @Test
    void findProducts_withValidIds_success() {

        // arrange
        int page = 0;
        int pageSize = 10;
        String productId1 = "PRODUCT-ID-1";
        String productId2 = "PRODUCT-ID-2";
        Product product1 = Product.builder().id(productId1).build();
        Product product2 = Product.builder().id(productId2).build();
        List<String> productIds = List.of(productId1, productId2);
        List<Product> products = List.of(product1, product2);

        // mock method call
        given(productRepository.findByIdIn(productIds, PageRequest.of(page, pageSize)))
                .willReturn(new PageImpl<>(products));

        // act
        PaginatedProductList productPage = productService.findProducts(
                productIds,
                page,
                pageSize
        );

        // assert
        assertNotNull(productPage);
        assertEquals(1, productPage.getTotalPages());
        assertEquals(products.size(), productPage.getSize());
        assertEquals(page, productPage.getNumber());
        assertNotNull(productPage.getProducts());
        assertEquals(products.size(), productPage.getProducts().size());
        assertEquals(productId1, productPage.getProducts().get(0).getId());
        assertEquals(productId2, productPage.getProducts().get(1).getId());
    }

    @Test
    void findProducts_withInvalidIds_success() {

        // arrange
        int page = 0;
        int pageSize = 10;
        String productId1 = "PRODUCT-ID-NOT-PRESENT-1";
        String productId2 = "PRODUCT-ID-NOT-PRESENT-2";
        List<String> productIds = List.of(productId1, productId2);
        List<Product> products = List.of();// empty list

        // mock method calls
        given(productRepository.findByIdIn(productIds, PageRequest.of(page, pageSize)))
                .willReturn(new PageImpl<>(products));

        // act
        PaginatedProductList productPage = productService.findProducts(
                productIds,
                page,
                pageSize
        );

        // assert
        assertNotNull(productPage);
        assertEquals(1, productPage.getTotalPages());
        assertEquals(products.size(), productPage.getSize());
        assertEquals(page, productPage.getNumber());
        assertNotNull(productPage.getProducts());
        assertEquals(products.size(), productPage.getProducts().size());
    }

    @Test
    void updateProducts_success() {
        // arrange
        String productId1 = "PR-1";
        String productId2 = "PR-2";
        String productCategoryId1 = "PC-1";
        String productCategoryId2 = "PC-2";
        String productImageId1 = "PI-1";
        String productImageId2 = "PI-1";
        String currencyId1 = "CUR-1";
        String currencyId2 = "CUR-2";

        List<UpdateProductDto> dtos = List.of(
                UpdateProductDto.builder()
                        .productId(productId1)
                        .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build(),

                UpdateProductDto.builder()
                        .productId(productId2)
                        .title("Apple 2023 MacBook Air Laptop with M2 chip  (Updated)")
                        .description("38.91 cm (15.3 inch) Liquid Retina Display, 8GB RAM 512GB SSD Storage. (Updated)")
                        .currencyId(currencyId2)
                        .price(15490000L)
                        .productImageIds(List.of(productImageId2))
                        .taggedCategoryIds(List.of(productCategoryId2))
                        .build()
        );
        Product product1 = Product.builder().id(productId1).createdOn(ZonedDateTime.now()).lastModifiedOn(ZonedDateTime.now()).build();
        Product product2 = Product.builder().id(productId2).createdOn(ZonedDateTime.now()).lastModifiedOn(ZonedDateTime.now()).build();
        ProductCategory productCategory1 = ProductCategory.builder().id(productCategoryId1).build();
        ProductCategory productCategory2 = ProductCategory.builder().id(productCategoryId1).build();
        ProductImage productImage1 = ProductImage.builder().id(productImageId1).build();
        ProductImage productImage2 = ProductImage.builder().id(productImageId2).build();
        Currency currency1 = Currency.builder().id(currencyId1).build();
        Currency currency2 = Currency.builder().id(currencyId2).build();

        // mock method calls
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        when(productRepository.saveAllAndFlush(
                Mockito.anyList())).then(i -> mockUpdate(i.getArgument(0, List.class)));
        when(productCategoryRepository.findById(productCategoryId1))
                .thenReturn(Optional.of(productCategory1));
        when(productCategoryRepository.findById(productCategoryId2))
                .thenReturn(Optional.of(productCategory2));
        when(productImageRepository.findById(productImageId1))
                .thenReturn(Optional.of(productImage1));
        when(productImageRepository.findById(productImageId2))
                .thenReturn(Optional.of(productImage2));
        when(currencyRepository.findById(currencyId1))
                .thenReturn(Optional.of(currency1));
        when(currencyRepository.findById(currencyId2))
                .thenReturn(Optional.of(currency2));

        // act
        List<ProductModel> productModels = productService.updateProducts(dtos);

        // assert
        assertNotNull(productModels);
        assertEquals(dtos.size(), productModels.size());
        assertNotNull(productModels.get(0).getId());
        assertNotNull(productModels.get(1).getId());
        assertNotNull(productModels.get(0).getCreatedOn());
        assertNotNull(productModels.get(1).getCreatedOn());
        assertNotNull(productModels.get(0).getLastModifiedOn());
        assertNotNull(productModels.get(1).getLastModifiedOn());
        assertEquals(dtos.get(0).getTitle(), productModels.get(0).getTitle());
        assertEquals(dtos.get(1).getTitle(), productModels.get(1).getTitle());
        assertEquals(dtos.get(0).getDescription(), productModels.get(0).getDescription());
        assertEquals(dtos.get(1).getDescription(), productModels.get(1).getDescription());
        assertEquals(dtos.get(0).getPrice(), productModels.get(0).getPrice().getPrice());
        assertEquals(dtos.get(1).getPrice(), productModels.get(1).getPrice().getPrice());
    }

    @Test
    void updateProducts_withInvalidProductId_fail() {
        // arrange
        String productId1 = "PR-NOT-PRESENT";
        String productCategoryId1 = "PC-1";
        String productImageId1 = "PI-1";
        String currencyId1 = "CUR-1";
        List<UpdateProductDto> dtos = List.of(
                UpdateProductDto.builder()
                        .productId(productId1)
                        .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build()
        );
        ProductCategory productCategory1 = ProductCategory.builder().id(productCategoryId1).build();

        // mock method calls
        when(productRepository.findById(productId1)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductNotFoundException.class, () -> productService.updateProducts(dtos));
    }


    @Test
    void updateProducts_invalidProductCategoryId_fail() {
        // arrange
        String productId1 = "PR-1";
        String productCategoryId1 = "PC-1";
        String productImageId1 = "PI-1";
        String currencyId1 = "CUR-1";

        List<UpdateProductDto> dtos = List.of(
                UpdateProductDto.builder()
                        .productId(productId1)
                        .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build()
        );
        Product product1 = Product.builder().id(productId1).createdOn(ZonedDateTime.now()).lastModifiedOn(ZonedDateTime.now()).build();
        ProductImage productImage1 = ProductImage.builder().id(productImageId1).build();
        Currency currency1 = Currency.builder().id(currencyId1).build();

        // mock method calls
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productCategoryRepository.findById(productCategoryId1))
                .thenReturn(Optional.empty());
        when(productImageRepository.findById(productImageId1))
                .thenReturn(Optional.of(productImage1));
        when(currencyRepository.findById(currencyId1))
                .thenReturn(Optional.of(currency1));
        // act & assert
        assertThrows(ProductCategoryNotFoundException.class, () -> productService.updateProducts(dtos));
    }

    @Test
    void updateProducts_withInvalidProductImageId_fail() {
        // arrange
        String productId1 = "PR-1";
        String productCategoryId1 = "PC-1";
        String productImageId1 = "PI-NOT-PRESENT";
        String currencyId1 = "CUR-1";

        List<UpdateProductDto> dtos = List.of(
                UpdateProductDto.builder()
                        .productId(productId1)
                        .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build()
        );
        Product product1 = Product.builder().id(productId1).createdOn(ZonedDateTime.now()).lastModifiedOn(ZonedDateTime.now()).build();
        Currency currency1 = Currency.builder().id(currencyId1).build();

        // mock method calls
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productImageRepository.findById(productImageId1))
                .thenReturn(Optional.empty());
        when(currencyRepository.findById(currencyId1))
                .thenReturn(Optional.of(currency1));

        // act & assert
        assertThrows(ProductImageNotFoundException.class, () -> productService.updateProducts(dtos));
    }


    @Test
    void updateProducts_withInvalidCurrencyId_fail() {
        // arrange
        String productId1 = "PR-1";
        String productCategoryId1 = "PC-1";
        String productImageId1 = "PI-1";
        String currencyId1 = "CUR-NOT-PRESENT";

        List<UpdateProductDto> dtos = List.of(
                UpdateProductDto.builder()
                        .productId(productId1)
                        .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                        .currencyId(currencyId1)
                        .price(9299000L)
                        .productImageIds(List.of(productImageId1))
                        .taggedCategoryIds(List.of(productCategoryId1))
                        .build()
        );
        Product product1 = Product.builder().id(productId1).createdOn(ZonedDateTime.now()).lastModifiedOn(ZonedDateTime.now()).build();

        // mock method calls
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));

        when(currencyRepository.findById(currencyId1))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(CurrencyNotFoundException.class, () -> productService.updateProducts(dtos));
    }

    @Test
    void deleteProducts_success() {

        // arrange
        String productId1 = "PR-1";
        String productId2 = "PR-2";
        List<DeleteProductDto> dtos = List.of(
                DeleteProductDto.builder().productId(productId1).build(),
                DeleteProductDto.builder().productId(productId2).build()
        );
        Product product1 = Product.builder().id(productId1).build();
        Product product2 = Product.builder().id(productId2).build();

        // mock method calls
        when(productRepository.findById(productId1))
                .thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2))
                .thenReturn(Optional.of(product2));

        // act
        List<ProductModel> productModels = productService.deleteProducts(dtos);

        // assert
        assertNotNull(productModels);
        assertEquals(dtos.size(), productModels.size());
    }

    @Test
    void deleteProducts_withInvalidProductId_fail() {

        // arrange
        String productId1 = "PR-NOT-PRESENT";
        List<DeleteProductDto> dtos = List.of(
                DeleteProductDto.builder().productId(productId1).build()
        );

        // mock method calls
        when(productRepository.findById(productId1))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProducts(dtos));
    }

    /**
     * Helper method that mocks saving List of Product entity by setting the id, createdOn, lastModified on fields
     *
     * @param products
     * @return
     */
    private List<Product> mockSave(List<Product> products) {

        return products.stream()
                .map(this::mockSave)
                .collect(Collectors.toList());
    }

    /**
     * Helper method that mocks saving of a Product entity by setting the id, createdOn, lastModified on fields
     *
     * @param product
     * @return
     */
    private Product mockSave(Product product) {

        product.setId(UUID.randomUUID().toString());
        product.setCreatedOn(ZonedDateTime.now());
        product.setLastModifiedOn(ZonedDateTime.now());
        return product;
    }


    /**
     * Helper method that mocks saving List of Product entity by setting a new value in lastModified field
     *
     * @param products
     * @return
     */
    private List<Product> mockUpdate(List<Product> products) {

        return products.stream()
                .map(this::mockUpdate)
                .collect(Collectors.toList());
    }

    /**
     * Helper method that mocks saving of a Product entity by setting a new value in lastModified field
     *
     * @param product
     * @return
     */
    private Product mockUpdate(Product product) {

        product.setLastModifiedOn(ZonedDateTime.now());
        return product;
    }
}