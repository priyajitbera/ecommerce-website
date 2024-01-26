package com.priyajit.ecommerce.user.management;

import com.priyajit.ecommerce.user.management.repository.DbEnvironmentConfigurationRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }

    private DbEnvironmentConfigurationRepository dbEnvironmentConfigurationRepository;
}
