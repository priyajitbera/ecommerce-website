package com.priyajit.order.management.service.mongorepository;

import com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEnvironmentConfigurationRepository extends MongoRepository<DbEnvironmentConfiguration, Long> {
}
