package com.priyajit.ecommerce.cart.service.mongorepository;

import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEnvironmentConfigurationRepository extends MongoRepository<DbEnvironmentConfiguration, Long> {
}
