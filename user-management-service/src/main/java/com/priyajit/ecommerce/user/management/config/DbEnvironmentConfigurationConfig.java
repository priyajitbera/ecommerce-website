package com.priyajit.ecommerce.user.management.config;

import com.priyajit.ecommerce.user.management.component.DbEnvironmentConfigurationProvider;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DbEnvironmentConfigurationConfig {

    private DbEnvironmentConfigurationProvider dbEnvironmentConfigurationProvider;


    public DbEnvironmentConfigurationConfig(
            DbEnvironmentConfigurationProvider environmentConfigurationProvider
    ) {
        this.dbEnvironmentConfigurationProvider = environmentConfigurationProvider;
    }

    @Bean
    public DbEnvironmentConfiguration dbEnvironmentConfiguration() {
        log.info("DbEnvironmentConfigurationConfig.dbEnvironmentConfiguration()");
        return dbEnvironmentConfigurationProvider.getActiveDbEnvironmentConfiguration();
    }
}
