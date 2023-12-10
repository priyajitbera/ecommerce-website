package com.priyajit.product.ecommerce.catalog.service.service;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.UpdateProductDto;
import com.priyajit.product.ecommerce.catalog.service.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findProducts(List<Long> productIds, int pageIndex, int pageSize);

    List<String> findProductCategories();

    List<Product> findProductByCategories(String category);

    List<Product> createProducts(List<CreateProductDto> dtoList);

    Product updateProduct(Long productId, UpdateProductDto dto);

    Product deleteProduct(Long productId);
}
