package com.priyajit.ecommerce.user.management.component;

import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;

public interface DbEnvironmentConfigurationProvider {

    DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration();
}
