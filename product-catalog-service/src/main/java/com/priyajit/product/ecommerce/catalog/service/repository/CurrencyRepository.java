package com.priyajit.product.ecommerce.catalog.service.repository;

import com.priyajit.product.ecommerce.catalog.service.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
