package com.priyajit.ecommerce.user.management;

import com.priyajit.ecommerce.user.management.component.UserAuthTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserManagementServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }

    @Autowired
    UserAuthTokenProvider userAuthTokenProvider;

    @Override
    public void run(String... args) throws Exception {
    }
}
