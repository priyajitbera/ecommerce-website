package com.priyajit.ecommerce.user.management;

import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfigurationProperty;
import com.priyajit.ecommerce.user.management.repository.DbEnvironmentConfigurationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class UserManagementServiceApplication{

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }

    private DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;
}
