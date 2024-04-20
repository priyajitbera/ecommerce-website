package com.priyajit.ecommerce.payment.service;

import com.priyajit.ecommerce.payment.service.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.payment.service.entity.DbEnvironmentConfigurationProperty;
import com.priyajit.ecommerce.payment.service.repository.DbEnvironmentConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class PaymentServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    @Autowired
    private DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;

    @Override
    public void run(String... args) throws Exception {

    }
}
