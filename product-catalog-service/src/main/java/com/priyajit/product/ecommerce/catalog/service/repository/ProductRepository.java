package com.priyajit.product.ecommerce.catalog.service.repository;

import com.priyajit.product.ecommerce.catalog.service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByIdIn(List<String> ids, PageRequest pageRequest);
}
