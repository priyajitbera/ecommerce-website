package com.priyajit.product.ecommerce.catalog.service.repository;

import com.priyajit.product.ecommerce.catalog.service.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
}
