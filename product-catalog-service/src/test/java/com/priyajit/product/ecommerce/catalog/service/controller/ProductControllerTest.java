package com.priyajit.product.ecommerce.catalog.service.controller;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductDto;
import com.priyajit.product.ecommerce.catalog.service.model.ProductModel;
import com.priyajit.product.ecommerce.catalog.service.repository.ProductRepository;
import com.priyajit.product.ecommerce.catalog.service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void contextLoads() {
        assertNotNull(productController);
    }

    @Test
    void createProducts() {

        List<CreateProductDto> dtos = List.of(
                CreateProductDto.builder()
                        .title("Apple iPhone 15 Plus (256 GB) - Blue")
                        .description("DYNAMIC ISLAND COMES TO IPHONE 15")
                        .currencyId("CUR1")
                        .price(9299000L)
                        .productImageIds(List.of("PI-1", "PI-2"))
                        .taggedCategoryIds(List.of("PC-1", "PC-2"))
                        .build()
        );

        BDDMockito.given(productRepository.saveAllAndFlush(List.of()))
                .willReturn(List.of());

        List<ProductModel> models = productController.createProducts(dtos);

        assertNotNull(models);
    }
}