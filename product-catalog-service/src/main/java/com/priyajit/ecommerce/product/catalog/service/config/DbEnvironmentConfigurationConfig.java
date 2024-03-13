package com.priyajit.ecommerce.product.catalog.service.config;

import com.priyajit.ecommerce.product.catalog.service.component.DbEnvironmentConfigurationProvider;
import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbEnvironmentConfigurationConfig {

    @Bean
    DbEnvironmentConfiguration dbEnvironmentConfiguration(DbEnvironmentConfigurationProvider dbEnvironmentConfigurationProvider) {
        return dbEnvironmentConfigurationProvider.getActiveDbEnvironmentConfiguration();
    }
}
