package com.priyajit.ecommerce.email.service.config;

import com.priyajit.ecommerce.email.service.component.DbEnvironmentConfigurationProvider;
import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbEnvironmentConfigurationConfig {

    @Bean
    DbEnvironmentConfiguration dbEnvironmentConfiguration(DbEnvironmentConfigurationProvider dbEnvironmentConfigurationProvider) {
        return dbEnvironmentConfigurationProvider.getActiveDbEnvironmentConfiguration();
    }
}
