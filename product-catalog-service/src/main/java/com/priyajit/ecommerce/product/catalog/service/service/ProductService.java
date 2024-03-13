package com.priyajit.ecommerce.product.catalog.service.service;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.DeleteProductDto;
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

    List<ProductModel> createProducts(List<CreateProductDto> dtoList);

    List<ProductModel> updateProducts(List<UpdateProductDto> dtos);

    List<ProductModel> deleteProducts(List<DeleteProductDto> dtos);

    PaginatedProductList search(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable String productDescriptionPart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex, int pageSize
    );

    IndexProductsInElasticSearchModel indexProductsInElasticSearch(IndexProductsInElasticSearchDto indexProductsInElasticSearchDto);

    DeleteProductsInElasticSearchModel deleteProductsInElasticSearch(IndexProductsInElasticSearchDto dto);
}
