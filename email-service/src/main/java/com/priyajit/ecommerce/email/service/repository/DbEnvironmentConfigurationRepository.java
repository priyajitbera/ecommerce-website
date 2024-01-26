package com.priyajit.ecommerce.email.service.repository;

import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfigurationProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEnvironmentConfigurationRepository extends JpaRepository<DbEnvironmentConfigurationProperty, Long> {
}
