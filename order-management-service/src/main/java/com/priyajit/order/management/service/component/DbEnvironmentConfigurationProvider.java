package com.priyajit.order.management.service.component;

import com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration;

public interface DbEnvironmentConfigurationProvider {

    DbEnvironmentConfiguration getActiveDbEnvironmentConfiguration();
}
