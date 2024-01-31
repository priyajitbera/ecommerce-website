package com.priyajit.product.ecommerce.catalog.service.component.impl;

import com.priyajit.product.ecommerce.catalog.service.component.DbEnvironmentConfigurationProvider;
import com.priyajit.product.ecommerce.catalog.service.entity.DbEnvironmentConfiguration;
import com.priyajit.product.ecommerce.catalog.service.repository.DbEnvironmentConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DbEnvironmentConfigurationProviderImplV1 implements DbEnvironmentConfigurationProvider {
    private DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;

    public DbEnvironmentConfigurationProviderImplV1(
            DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository
    ) {
        this.dbEnvironmentConfigurationRepository = dbEnvironmentConfigurationRepository;
    }

    @Override
    public DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration() {

        // fetch all configs from DB
        List<DbEnvironmentConfiguration> allConfigs
                = dbEnvironmentConfigurationRepository.findAll();

        // filter out the active configuration, expected to have only one active config
        List<DbEnvironmentConfiguration> activeConfigs = allConfigs.stream()
                .filter(DbEnvironmentConfiguration::getIsActive)
                .collect(Collectors.toList());

        // when no active configuration found
        if (activeConfigs.size() == 0) {
            log.error("No active EnvironmentConfiguration found");
            return null;
        }

        // when more than one active configuration found
        if (activeConfigs.size() > 1) {
            log.warn("{} active EnvironmentConfiguration found, expected is 1, selecting first one", activeConfigs.size());
        }

        DbEnvironmentConfiguration activeConfig = allConfigs.get(0);
        log.info("Selecting active EnvironmentConfiguration with id:{} version:{}",
                activeConfig.getId(), activeConfig.getVersion());

        return activeConfig;
    }
}
