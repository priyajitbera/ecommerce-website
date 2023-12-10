package com.priyajit.product.ecommerce.catalog.service.repository;

import com.priyajit.product.ecommerce.catalog.service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT DISTINCT(category) FROM PRODUCT", nativeQuery = true)
    List<String> findProductCategories();

    List<Product> findByCategory(String category);

    Page<Product> findByIdIn(List<Long> ids, PageRequest pageRequest);
}
