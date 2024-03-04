package com.priyajit.ecommerce.product.catalog.service.repository.querymethod;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepositoryQueryMethod extends JpaRepository<ProductImage, String> {
}
