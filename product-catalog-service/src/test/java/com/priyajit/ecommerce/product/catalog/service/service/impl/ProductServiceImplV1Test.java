package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.DeleteProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductImage;
import com.priyajit.ecommerce.product.catalog.service.exception.CurrencyNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductCategoryNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductImageNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.ProductRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.CurrencyRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductCategoryRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductImageRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductRepositoryQueryMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
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
    private ProductRepositoryQueryMethod productRepositoryQueryMethod;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod;
    @Mock
    private ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod;
    @Mock
    private CurrencyRepositoryQueryMethod currencyRepositoryQueryMethod;

    @InjectMocks
    private ProductServiceImplV1 productService;

    @Test
    void createProducts_success() {

        // arrange
        var productCategoryId = "PC-1";
        var productImageId = "PI-1";
        var currencyId = "INR";
        var price = BigDecimal.valueOf(92990);
        var userId = "U-1";

        var dto = CreateProductDto.builder()
                .title("Apple iPhone 15 Plus (256 GB) - Blue")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                .currencyId(currencyId)
                .price(price)
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        var productCategory = ProductCategory.builder().id(productCategoryId).build();
        var productImage = ProductImage.builder().id(productImageId).build();
        var currency = Currency.builder().id(currencyId).build();

        // mock method calls
        when(productRepositoryQueryMethod.saveAndFlush(
                Mockito.any(Product.class))).then(i -> mockSave(i.getArgument(0, Product.class)));
        when(productCategoryRepositoryQueryMethod.findById(productCategoryId))
                .thenReturn(Optional.of(productCategory));
        when(productImageRepositoryQueryMethod.findById(productImageId))
                .thenReturn(Optional.of(productImage));
        when(currencyRepositoryQueryMethod.findById(currencyId))
                .thenReturn(Optional.of(currency));

        // act
        var productModel = productService.createProduct(dto, userId);

        // assert
        assertNotNull(productModel);
        assertNotNull(productModel.getId());
        assertNotNull(productModel.getCreatedOn());
        assertNotNull(productModel.getLastModifiedOn());
        assertEquals(dto.getTitle(), productModel.getTitle());
        assertEquals(dto.getDescription(), productModel.getDescription());
        assertEquals(dto.getPrice(), productModel.getPrice().getPrice());
        assertEquals(dto.getTaggedCategoryIds().get(0), productModel.getTaggedCategories().get(0).getId());
    }

    @Test
    void createProducts_invalidProductCategoryId_fail() {

        // arrange
        var productCategoryId = "PC-NOT-PRESENT";
        var productImageId = "PI-1";
        var currencyId = "CUR-1";
        var userId = "U-1";

        var dto = CreateProductDto.builder()
                .title("Apple iPhone 15 Plus (256 GB) - Blue")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                .currencyId(currencyId)
                .price(BigDecimal.valueOf(92990))
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        // mock method calls
        when(productCategoryRepositoryQueryMethod.findById(productCategoryId))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductCategoryNotFoundException.class, () -> productService.createProduct(dto, userId));
    }

    @Test
    void createProducts_invalidProductImageId_fail() {

        // arrange
        var productCategoryId = "PC-NOT-PRESENT";
        var productImageId = "PI-NOT-PRESENT";
        var currencyId = "CUR-1";
        var userId = "U-1";

        var dto = CreateProductDto.builder()
                .title("Apple iPhone 15 Plus (256 GB) - Blue")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                .currencyId(currencyId)
                .price(BigDecimal.valueOf(92990))
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        var productCategory = ProductCategory.builder().id(productCategoryId).build();

        // mock method calls
        when(productCategoryRepositoryQueryMethod.findById(productCategoryId))
                .thenReturn(Optional.of(productCategory));
        when(productImageRepositoryQueryMethod.findById(productImageId))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductImageNotFoundException.class, () -> productService.createProduct(dto, userId));
    }


    @Test
    void createProducts_invalidCurrencyId_fail() {

        // arrange
        var productCategoryId = "PC-NOT-PRESENT";
        var productImageId = "PI-NOT-PRESENT";
        var currencyId = "CUR-NOT-PRESENT";
        var userId = "U-1";

        var dto = CreateProductDto.builder()
                .title("Apple iPhone 15 Plus (256 GB) - Blue")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                .currencyId(currencyId)
                .price(BigDecimal.valueOf(92990))
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        var productCategory = ProductCategory.builder().id(productCategoryId).build();
        var productImage = ProductImage.builder().id(productImageId).build();

        // mock method calls
        when(productCategoryRepositoryQueryMethod.findById(productCategoryId))
                .thenReturn(Optional.of(productCategory));
        when(productImageRepositoryQueryMethod.findById(productImageId))
                .thenReturn(Optional.of(productImage));
        when(currencyRepositoryQueryMethod.findById(currencyId))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(CurrencyNotFoundException.class, () -> productService.createProduct(dto, userId));
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
        given(productRepository.findProducts(productIds, null, null, null, page, pageSize))
                .willReturn(new PageImpl<>(products));

        // act
        PaginatedProductList productPage = productService.findProducts(
                productIds,
                null,
                null,
                null,
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
        given(productRepository.findProducts(productIds, null, null, null, page, pageSize))
                .willReturn(new PageImpl<>(products));

        // act
        PaginatedProductList productPage = productService.findProducts(
                productIds,
                null,
                null,
                null,
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
    void updateProduct_success() {
        // arrange
        var productId = "PR-1";
        var productCategoryId = "PC-1";
        var productImageId = "PI-1";
        var currencyId = "CUR-1";
        var userId = "U-1";

        var dto = UpdateProductDto.builder()
                .productId(productId)
                .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                .currencyId(currencyId)
                .price(BigDecimal.valueOf(92990))
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        Product product = Product.builder()
                .id(productId)
                .createdOn(ZonedDateTime.now())
                .lastModifiedOn(ZonedDateTime.now())
                .build();
        var productCategory = ProductCategory.builder().id(productCategoryId).build();
        var productImage = ProductImage.builder().id(productImageId).build();
        var currency = Currency.builder().id(currencyId).build();

        // mock method calls
        when(productRepositoryQueryMethod.findById(productId)).thenReturn(Optional.of(product));

        when(productRepositoryQueryMethod.saveAndFlush(
                Mockito.any(Product.class))).then(i -> mockUpdate(i.getArgument(0, Product.class)));

        when(productCategoryRepositoryQueryMethod.findById(productCategoryId))
                .thenReturn(Optional.of(productCategory));
        when(productImageRepositoryQueryMethod.findById(productImageId))
                .thenReturn(Optional.of(productImage));

        when(currencyRepositoryQueryMethod.findById(currencyId))
                .thenReturn(Optional.of(currency));
        // act
        var productModel = productService.updateProduct(dto, userId);

        // assert
        assertNotNull(productModel);
        assertNotNull(productModel.getId());
        assertNotNull(productModel.getCreatedOn());
        assertNotNull(productModel.getLastModifiedOn());
        assertEquals(dto.getTitle(), productModel.getTitle());
        assertEquals(dto.getDescription(), productModel.getDescription());
        assertEquals(dto.getPrice(), productModel.getPrice().getPrice());
    }

    @Test
    void updateProduct_withInvalidProductId_fail() {
        // arrange
        var productId = "PR-NOT-PRESENT";
        var productCategoryId1 = "PC-1";
        var productImageId1 = "PI-1";
        var currencyId1 = "CUR-1";
        var userId = "U-1";
        UpdateProductDto dto = UpdateProductDto.builder()
                .productId(productId)
                .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                .currencyId(currencyId1)
                .price(BigDecimal.valueOf(92990))
                .productImageIds(List.of(productImageId1))
                .taggedCategoryIds(List.of(productCategoryId1))
                .build();

        // mock method calls
        when(productRepositoryQueryMethod.findById(productId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(dto, userId));
    }


    @Test
    void updateProduct_invalidProductCategoryId_fail() {
        // arrange
        var productId = "PR-1";
        var productCategoryId = "PC-1";
        var productImageId = "PI-1";
        var currencyId = "CUR-1";
        var price = BigDecimal.valueOf(92990);
        var userId = "U-1";

        var dto = UpdateProductDto.builder()
                .productId(productId)
                .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                .currencyId(currencyId)
                .price(price)
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        var product = Product.builder()
                .id(productId)
                .createdOn(ZonedDateTime.now())
                .lastModifiedOn(ZonedDateTime.now())
                .build();
        var productImage = ProductImage.builder().id(productImageId).build();
        var currency = Currency.builder().id(currencyId).build();

        // mock method calls
        when(productRepositoryQueryMethod.findById(productId)).thenReturn(Optional.of(product));
        when(productCategoryRepositoryQueryMethod.findById(productCategoryId))
                .thenReturn(Optional.empty());
        when(productImageRepositoryQueryMethod.findById(productImageId))
                .thenReturn(Optional.of(productImage));
        when(currencyRepositoryQueryMethod.findById(currencyId))
                .thenReturn(Optional.of(currency));
        // act & assert
        assertThrows(ProductCategoryNotFoundException.class, () -> productService.updateProduct(dto, userId));
    }

    @Test
    void updateProduct_withInvalidProductImageId_fail() {
        // arrange
        var productId = "PR-1";
        var productCategoryId = "PC-1";
        var productImageId = "PI-NOT-PRESENT";
        var currencyId = "CUR-1";
        var userId = "U-1";

        var dto = UpdateProductDto.builder()
                .productId(productId)
                .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                .currencyId(currencyId)
                .price(BigDecimal.valueOf(92990))
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        Product product = Product.builder()
                .id(productId)
                .createdOn(ZonedDateTime.now())
                .lastModifiedOn(ZonedDateTime.now())
                .build();
        Currency currency = Currency.builder().id(currencyId).build();

        // mock method calls
        when(productRepositoryQueryMethod.findById(productId)).thenReturn(Optional.of(product));
        when(productImageRepositoryQueryMethod.findById(productImageId))
                .thenReturn(Optional.empty());
        when(currencyRepositoryQueryMethod.findById(currencyId))
                .thenReturn(Optional.of(currency));

        // act & assert
        assertThrows(ProductImageNotFoundException.class, () -> productService.updateProduct(dto, userId));
    }


    @Test
    void updateProduct_withInvalidCurrencyId_fail() {
        // arrange
        var productId = "PR-1";
        var productCategoryId = "PC-1";
        var productImageId = "PI-1";
        var currencyId = "CUR-NOT-PRESENT";
        var userId = "U-1";

        var dto = UpdateProductDto.builder()
                .productId(productId)
                .title("Apple iPhone 15 Plus (256 GB) - Blue (Updated)")
                .description("DYNAMIC ISLAND COMES TO IPHONE 15  (Updated)")
                .currencyId(currencyId)
                .price(BigDecimal.valueOf(92990))
                .productImageIds(List.of(productImageId))
                .taggedCategoryIds(List.of(productCategoryId))
                .build();

        Product product = Product.builder()
                .id(productId)
                .createdOn(ZonedDateTime.now())
                .lastModifiedOn(ZonedDateTime.now())
                .build();

        // mock method calls
        when(productRepositoryQueryMethod.findById(productId)).thenReturn(Optional.of(product));

        when(currencyRepositoryQueryMethod.findById(currencyId))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(CurrencyNotFoundException.class, () -> productService.updateProduct(dto, userId));
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
        when(productRepositoryQueryMethod.findById(productId1))
                .thenReturn(Optional.of(product1));
        when(productRepositoryQueryMethod.findById(productId2))
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
        when(productRepositoryQueryMethod.findById(productId1))
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