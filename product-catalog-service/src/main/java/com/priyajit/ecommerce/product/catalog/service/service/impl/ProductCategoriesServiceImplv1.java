package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductCategoryDto;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductCategoryNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.ProductCategoryModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.ProductCategoryRepository;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductCategoryRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.service.ProductCategoryService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductCategoriesServiceImplv1 implements ProductCategoryService {

    private ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod;
    private ProductCategoryRepository productCategoryRepository;


    public ProductCategoriesServiceImplv1(
            ProductCategoryRepositoryQueryMethod productCategoryRepositoryQueryMethod,
            ProductCategoryRepository productCategoryRepository
    ) {
        this.productCategoryRepositoryQueryMethod = productCategoryRepositoryQueryMethod;
        this.productCategoryRepository = productCategoryRepository;
    }

    /**
     * Method to create ProductCategories
     *
     * @param dtos
     * @return
     */
    @Override
    public List<ProductCategoryModel> createProductCategories(List<CreateProductCategoryDto> dtos) {

        List<ProductCategory> productCategories = dtos.stream()
                .filter(Objects::nonNull)
                .map(this::createProductCategoryFromDto)
                .collect(Collectors.toList());

        return productCategoryRepositoryQueryMethod.saveAllAndFlush(productCategories).stream()
                .map(ProductCategoryModel::from)
                .collect(Collectors.toList());
    }

    /**
     * Method to search ProductCategories with ids or name
     *
     * @param ids
     * @param names
     * @return
     */
    @Override
    public List<ProductCategoryModel> findProductCategories(List<String> ids, List<String> names) {

        return productCategoryRepository.findProductCategories(ids, names).stream()
                .map(ProductCategoryModel::from)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to create ProductCategory object from CreateProductCategoryDto
     *
     * @param dto
     * @return
     */
    private ProductCategory createProductCategoryFromDto(CreateProductCategoryDto dto) {

        ProductCategory parentCategory = fetchParentCategory(dto.getParentCategoryId());

        return ProductCategory.builder()
                .name(dto.getName())
                .parentCategory(parentCategory)
                .build();
    }

    /**
     * Helper method to fetch productCategory
     *
     * @param productCategoryId
     * @return
     */
    private ProductCategory fetchParentCategory(@Nullable String productCategoryId) {
        if (productCategoryId == null) return null;

        return productCategoryRepositoryQueryMethod.findById(productCategoryId)
                .orElseThrow(ProductCategoryNotFoundException.supplier(productCategoryId));
    }
}
