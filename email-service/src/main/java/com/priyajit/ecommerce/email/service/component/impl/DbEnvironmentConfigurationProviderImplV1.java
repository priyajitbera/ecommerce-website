package com.priyajit.ecommerce.email.service.component.impl;

import com.priyajit.ecommerce.email.service.component.DbEnvironmenConfigurationProvider;
import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.email.service.repository.DbEnvironmentConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class DbEnvironmentConfigurationProviderImplV1 implements DbEnvironmenConfigurationProvider {
    private DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;

    public DbEnvironmentConfigurationProviderImplV1(
            DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository
    ) {
        this.dbEnvironmentConfigurationRepository = dbEnvironmentConfigurationRepository;
    }

    @Override
    public DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration() {

        List<DbEnvironmentConfiguration> allConfigs
                = dbEnvironmentConfigurationRepository.findAll();

        List<DbEnvironmentConfiguration> activeConfigs = allConfigs.stream()
                .filter(DbEnvironmentConfiguration::getIsActive)
                .collect(Collectors.toList());

        // expected to have only one active config
        if (activeConfigs.size() == 0) {
            log.error("No active EnvironmentConfiguration found");
            return null;
        }
        if (activeConfigs.size() > 1) {
            log.warn("{} active EnvironmentConfiguration found, expected is 1, selecting first one");
        }

        DbEnvironmentConfiguration activeConfig = allConfigs.get(0);
        log.info("Selecting active EnvironmentConfiguration with id:{} version:{}",
                activeConfig.getId(), activeConfig.getVersion());

        return activeConfig;
    }
}
