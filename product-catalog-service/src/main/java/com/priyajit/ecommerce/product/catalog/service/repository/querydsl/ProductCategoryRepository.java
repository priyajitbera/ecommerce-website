package com.priyajit.ecommerce.product.catalog.service.repository.querydsl;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ProductCategoryRepository {

    List<ProductCategory> findProductCategories(
            @Nullable List<String> ids,
            @Nullable List<String> names
    );
}
