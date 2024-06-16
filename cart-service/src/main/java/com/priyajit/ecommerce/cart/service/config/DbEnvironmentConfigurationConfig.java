package com.priyajit.ecommerce.cart.service.config;

import com.priyajit.ecommerce.cart.service.component.DbEnvironmentConfigurationProvider;
import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DbEnvironmentConfigurationConfig {

    @Bean
    public DbEnvironmentConfiguration dbEnvironmentConfiguration(DbEnvironmentConfigurationProvider dbEnvironmentConfigurationProvider) {
        log.info("Configuring dbEnvironmentConfiguration");
        return dbEnvironmentConfigurationProvider.getActiveDbEnvironmentConfiguration();
    }
}
