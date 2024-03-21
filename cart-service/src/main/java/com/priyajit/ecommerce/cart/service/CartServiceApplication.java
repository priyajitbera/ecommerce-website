package com.priyajit.ecommerce.cart.service;

import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.cart.service.mongorepository.DbEnvironmentConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class CartServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }

    @Autowired
    DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;
    @Override
    public void run(String... args) throws Exception {

//        var config = DbEnvironmentConfiguration.builder()
//                .version("1")
//                .isActive(true)
//                .properties(List.of(
//                        DbEnvironmentConfiguration.Property.builder()
//                                .key(DbEnvironmentConfiguration.Keys.PRODUCT_CATALOG_SERVICE_BASE_URL)
//                                .value("localhost:8081")
//                                .build()
//                ))
//                .build();
//
//        dbEnvironmentConfigurationRepository.save(config);
//        System.out.println(config.getId());
    }
}
