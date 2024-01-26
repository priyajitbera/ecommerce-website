package com.priyajit.ecommerce.user.management.repository;

import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEnvironmentConfigurationRepository extends JpaRepository<DbEnvironmentConfiguration, Long> {
}
