package com.priyajit.ecommerce.product.catalog.service.repository;

import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    List<Currency> findAllByIdInOrNameIn(List<String> ids, List<String> names);
}
