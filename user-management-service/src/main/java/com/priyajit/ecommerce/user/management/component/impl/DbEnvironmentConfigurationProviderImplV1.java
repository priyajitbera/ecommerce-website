package com.priyajit.ecommerce.user.management.component.impl;

import com.priyajit.ecommerce.user.management.component.DbEnvironmentConfigurationProvider;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.user.management.repository.DbEnvironmentConfigurationRepository;
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
