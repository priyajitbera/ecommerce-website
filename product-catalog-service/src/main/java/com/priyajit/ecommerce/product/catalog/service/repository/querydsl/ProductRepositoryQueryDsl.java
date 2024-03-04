package com.priyajit.ecommerce.product.catalog.service.repository.querydsl;

import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ProductRepositoryQueryDsl {

    Page<Product> findProducts(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex,
            int pageSize
    );
}
