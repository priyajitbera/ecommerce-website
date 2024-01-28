package com.priyajit.product.ecommerce.catalog.service.repository;

import com.priyajit.product.ecommerce.catalog.service.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
}
