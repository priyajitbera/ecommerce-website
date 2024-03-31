package com.priyajit.order.management.service.config;

import com.priyajit.order.management.service.component.DbEnvironmentConfigurationProvider;
import com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbEnvironmentConfigurationConfig {

    @Bean
    DbEnvironmentConfiguration dbEnvironmentConfiguration(DbEnvironmentConfigurationProvider dbEnvironmentConfigurationProvider) {
        return dbEnvironmentConfigurationProvider.getActiveDbEnvironmentConfiguration();
    }
}
