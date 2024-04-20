package com.priyajit.ecommerce.payment.service.repository;

import com.priyajit.ecommerce.payment.service.entity.DbEnvironmentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEnvironmentConfigurationRepository extends JpaRepository<DbEnvironmentConfiguration, Long> {
}
