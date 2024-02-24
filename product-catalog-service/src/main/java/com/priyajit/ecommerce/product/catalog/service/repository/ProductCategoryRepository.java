package com.priyajit.ecommerce.product.catalog.service.repository;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    List<ProductCategory> findByIdInOrNameIn(List<String> ids, List<String> names);
}
