package com.priyajit.ecommerce.product.catalog.service.redisrepository;

import com.priyajit.ecommerce.product.catalog.service.model.ProductModel;
import org.springframework.data.repository.CrudRepository;

public interface ProductModelRedisRepository extends CrudRepository<ProductModel, String> {
}
