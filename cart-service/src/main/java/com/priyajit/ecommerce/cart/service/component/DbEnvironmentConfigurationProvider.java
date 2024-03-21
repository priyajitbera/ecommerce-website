package com.priyajit.ecommerce.cart.service.component;

import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;

public interface DbEnvironmentConfigurationProvider {

    DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration();
}
