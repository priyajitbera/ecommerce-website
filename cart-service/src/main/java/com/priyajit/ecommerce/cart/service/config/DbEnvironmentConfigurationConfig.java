package com.priyajit.ecommerce.cart.service.config;

import com.priyajit.ecommerce.cart.service.component.DbEnvironmentConfigurationProvider;
import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbEnvironmentConfigurationConfig {

    @Bean
    DbEnvironmentConfiguration dbEnvironmentConfiguration(DbEnvironmentConfigurationProvider dbEnvironmentConfigurationProvider) {
        return dbEnvironmentConfigurationProvider.getActiveDbEnvironmentConfiguration();
    }
}
