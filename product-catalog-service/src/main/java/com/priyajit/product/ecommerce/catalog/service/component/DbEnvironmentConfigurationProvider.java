package com.priyajit.product.ecommerce.catalog.service.component;

import com.priyajit.product.ecommerce.catalog.service.entity.DbEnvironmentConfiguration;

public interface DbEnvironmentConfigurationProvider {

    DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration();
}
