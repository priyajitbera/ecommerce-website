package com.priyajit.ecommerce.product.catalog.service.component;

import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;

public interface DbEnvironmentConfigurationProvider {

    DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration();
}
