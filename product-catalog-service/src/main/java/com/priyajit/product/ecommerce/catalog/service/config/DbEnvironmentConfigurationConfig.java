package com.priyajit.product.ecommerce.catalog.service.config;

import com.priyajit.product.ecommerce.catalog.service.component.DbEnvironmentConfigurationProvider;
import com.priyajit.product.ecommerce.catalog.service.entity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbEnvironmentConfigurationConfig {

    @Bean
    DbEnvironmentConfiguration dbEnvironmentConfiguration(DbEnvironmentConfigurationProvider dbEnvironmentConfigurationProvider) {
        return dbEnvironmentConfigurationProvider.getActiveDbEnvironmentConfiguration();
    }
}
