package com.priyajit.ecommerce.email.service.component;

import com.priyajit.ecommerce.email.service.enitity.DbEnvironmentConfiguration;

public interface DbEnvironmentConfigurationProvider {

    DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration();
}
