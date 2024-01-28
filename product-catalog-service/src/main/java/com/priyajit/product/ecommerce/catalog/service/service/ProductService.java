package com.priyajit.product.ecommerce.catalog.service.service;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.DeleteProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.UpdateProductDto;
import com.priyajit.product.ecommerce.catalog.service.model.PaginatedProductList;
import com.priyajit.product.ecommerce.catalog.service.model.ProductModel;

import java.util.List;

public interface ProductService {
    PaginatedProductList findProducts(List<String> productIds, int pageIndex, int pageSize);

    List<ProductModel> createProducts(List<CreateProductDto> dtoList);

    List<ProductModel> updateProducts(List<UpdateProductDto> dtos);

    List<ProductModel> deleteProducts(List<DeleteProductDto> dtos);
}
