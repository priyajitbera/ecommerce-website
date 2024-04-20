package com.priyajit.ecommerce.payment.service.component;

import com.priyajit.ecommerce.payment.service.entity.DbEnvironmentConfiguration;

public interface DbEnvironmentConfigurationProvider {

    DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration();
}
