package com.priyajit.ecommerce.cart.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.priyajit.ecommerce.cart.service.mongorepository")
public class MongoConfig {
}
