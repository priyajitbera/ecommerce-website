package com.priyajit.ecommerce.product.catalog.service.service;

import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.IndexProductsDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;
import com.priyajit.ecommerce.product.catalog.service.model.IndexedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.model.SellerProductList;
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

    SellerProductList findSellersProducts(
            String userId,
            List<String> productIds,
            String productNamePart,
            List<String> produdctCategoryIds,
            List<String> productCategoryNames,
            Integer pageIndex, Integer pageSize
    );

    ProductModel createProduct(CreateProductDto dto, String userId);

    ProductModel updateProduct(UpdateProductDto dto, String userId);

    PaginatedProductList search(String searchKeyWord, int pageIndex, int pageSize);

    IndexedProductList indexProductsInElasticSearch(IndexProductsDto indexProductsDto, String userId);

    IndexedProductList deIndexProductsInElasticSearch(IndexProductsDto indexProductsDto, String userId);

    ProductModel findOneById(String productId);
}
