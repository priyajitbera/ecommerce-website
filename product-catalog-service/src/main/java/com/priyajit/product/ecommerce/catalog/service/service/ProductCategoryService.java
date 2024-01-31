package com.priyajit.product.ecommerce.catalog.service.service;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductCategoryDto;
import com.priyajit.product.ecommerce.catalog.service.model.ProductCategoryModel;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryModel> createProductCategories(List<CreateProductCategoryDto> dtos);

    List<ProductCategoryModel> findProductCategories(List<String> ids, List<String> names);
}
