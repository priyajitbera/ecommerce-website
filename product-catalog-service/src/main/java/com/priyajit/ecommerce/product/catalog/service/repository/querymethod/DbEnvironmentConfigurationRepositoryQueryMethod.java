package com.priyajit.ecommerce.product.catalog.service.repository.querymethod;

import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEnvironmentConfigurationRepositoryQueryMethod extends JpaRepository<DbEnvironmentConfiguration, Long> {
}
