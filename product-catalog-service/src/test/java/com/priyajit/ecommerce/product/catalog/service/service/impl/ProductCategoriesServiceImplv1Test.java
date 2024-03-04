package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductCategoryDto;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import com.priyajit.ecommerce.product.catalog.service.model.ProductCategoryModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductCategoryRepositoryQueryMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCategoriesServiceImplv1Test {

    @Mock
    private ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod;

    @InjectMocks
    private ProductCategoriesServiceImplv1 productCategoriesService;

    @Test
    void createProductCategories() {

        // arrange
        String parentCategoryId = "PC-ELECTRONICS";
        List<CreateProductCategoryDto> dtos = List.of(
                CreateProductCategoryDto.builder()
                        .name("Smartphones")
                        .build(),
                CreateProductCategoryDto.builder()
                        .name("Laptops")
                        .parentCategoryId(parentCategoryId)
                        .build()
        );
        ProductCategory parentCategory = ProductCategory.builder()
                .id(parentCategoryId)
                .name("Electronics")
                .build();

        // mock method calls
        when(productCategoryRepositoryQueryMethod.findById(parentCategoryId))
                .thenReturn(Optional.of(parentCategory));
        when(productCategoryRepositoryQueryMethod.saveAllAndFlush(Mockito.anyList()))
                .then(i -> mockSave(i.getArgument(0, List.class)));

        // act
        List<ProductCategoryModel> productCategoryModels = productCategoriesService.createProductCategories(dtos);

        // assert
        assertNotNull(productCategoryModels);
        assertEquals(dtos.size(), productCategoryModels.size());
        assertEquals(dtos.get(0).getName(), productCategoryModels.get(0).getName());
        assertEquals(dtos.get(1).getName(), productCategoryModels.get(1).getName());
        assertNull(productCategoryModels.get(0).getParentCategory());
        assertEquals(dtos.get(1).getParentCategoryId(), productCategoryModels.get(1).getParentCategory().getId());
    }

    @Test
    void findProductCategories_byIds_success() {
        String id1 = "PC-1";
        String id2 = "PC-2";
        String name1 = "Clothing";
        String name2 = "Electronics";
        List<String> ids = List.of(id1, id2);
        List<String> names = List.of(name1, name2);
        List<ProductCategory> productCategories = List.of(
                ProductCategory.builder()
                        .id(id1)
                        .name(name1)
                        .build(),
                ProductCategory.builder()
                        .id(id2)
                        .name(name2)
                        .build()
        );
        when(productCategoryRepositoryQueryMethod.findByIdInOrNameIn(ids, null))
                .thenReturn(productCategories);
        List<ProductCategoryModel> productCategoryModels = productCategoriesService.findProductCategories(ids, null);
        assertNotNull(productCategoryModels);
        assertEquals(ids.size(), productCategoryModels.size());
        assertEquals(ids.get(0), productCategoryModels.get(0).getId());
        assertEquals(ids.get(1), productCategoryModels.get(1).getId());
    }

    @Test
    void findProductCategories_byNames_success() {
        String id1 = "PC-1";
        String id2 = "PC-2";
        String name1 = "Clothing";
        String name2 = "Electronics";
        List<String> ids = List.of(id1, id2);
        List<String> names = List.of(name1, name2);
        List<ProductCategory> productCategories = List.of(
                ProductCategory.builder()
                        .id(id1)
                        .name(name1)
                        .build(),
                ProductCategory.builder()
                        .id(id2)
                        .name(name2)
                        .build()
        );
        when(productCategoryRepositoryQueryMethod.findByIdInOrNameIn(null, names))
                .thenReturn(productCategories);
        List<ProductCategoryModel> productCategoryModels = productCategoriesService.findProductCategories(null, names);
        assertNotNull(productCategoryModels);
        assertEquals(ids.size(), productCategoryModels.size());

        assertEquals(ids.get(0), productCategoryModels.get(0).getId());
        assertEquals(ids.get(1), productCategoryModels.get(1).getId());

        assertEquals(names.get(0), productCategoryModels.get(0).getName());
        assertEquals(names.get(1), productCategoryModels.get(1).getName());
    }

    /**
     * @param productCategories
     * @return
     */
    private List<ProductCategory> mockSave(List<ProductCategory> productCategories) {

        return productCategories.stream()
                .map(this::mockSave)
                .collect(Collectors.toList());
    }

    /**
     * @param productCategory
     * @return
     */
    private ProductCategory mockSave(ProductCategory productCategory) {

        productCategory.setId(UUID.randomUUID().toString());
        productCategory.setCreatedOn(ZonedDateTime.now());
        productCategory.setLastModifiedOn(ZonedDateTime.now());

        return productCategory;
    }
}