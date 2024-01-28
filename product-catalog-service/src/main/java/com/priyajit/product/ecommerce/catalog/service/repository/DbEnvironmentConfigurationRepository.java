package com.priyajit.product.ecommerce.catalog.service.repository;

import com.priyajit.product.ecommerce.catalog.service.entity.DbEnvironmentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEnvironmentConfigurationRepository extends JpaRepository<DbEnvironmentConfiguration, Long> {
}
