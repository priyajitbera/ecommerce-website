package com.priyajit.ecommerce.product.catalog.service.repository.querymethod;

import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepositoryQueryMethod extends JpaRepository<Product, String> {

    Page<Product> findByIdIn(List<String> ids, PageRequest pageRequest);
}
