package com.priyajit.ecommerce.product.catalog.service.service;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.IndexProductsInElasticSearchDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.model.DeleteProductsInElasticSearchModel;
import com.priyajit.ecommerce.product.catalog.service.model.IndexProductsInElasticSearchModel;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ProductService {
    PaginatedProductList findProducts(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex,
            int pageSize
    );

    ProductModel createProduct(CreateProductDto dto, String userId);

    ProductModel updateProduct(UpdateProductDto dto, String userId);

    PaginatedProductList search(String searchKeyWord, int pageIndex, int pageSize);

    IndexProductsInElasticSearchModel indexProductsInElasticSearch(IndexProductsInElasticSearchDto indexProductsInElasticSearchDto);

    DeleteProductsInElasticSearchModel deleteProductsInElasticSearch(IndexProductsInElasticSearchDto dto);

    ProductModel findOneById(String productId);
}
