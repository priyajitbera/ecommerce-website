package com.priyajit.ecommerce.product.catalog.service.service;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductCategoryDto;
import com.priyajit.ecommerce.product.catalog.service.model.ProductCategoryModel;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryModel> createProductCategories(List<CreateProductCategoryDto> dtos);

    List<ProductCategoryModel> findProductCategories(List<String> ids, List<String> names);
}
