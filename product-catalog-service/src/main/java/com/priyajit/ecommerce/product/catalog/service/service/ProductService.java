package com.priyajit.ecommerce.product.catalog.service.service;

import com.priyajit.ecommerce.product.catalog.service.dto.DeleteProductDto;
import com.priyajit.ecommerce.product.catalog.service.model.PaginatedProductList;
import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import com.priyajit.ecommerce.product.catalog.service.dto.CreateProductDto;
import com.priyajit.ecommerce.product.catalog.service.dto.UpdateProductDto;

import java.util.List;

public interface ProductService {
    PaginatedProductList findProducts(List<String> productIds, int pageIndex, int pageSize);

    List<ProductModel> createProducts(List<CreateProductDto> dtoList);

    List<ProductModel> updateProducts(List<UpdateProductDto> dtos);

    List<ProductModel> deleteProducts(List<DeleteProductDto> dtos);
}
