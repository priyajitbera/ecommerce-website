package com.priyajit.ecommerce.user.management.service.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    private Environment environment;

    public PasswordEncoderConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder.BCryptVersion bCryptVersion = BCryptPasswordEncoder.BCryptVersion.valueOf(
                environment.getProperty("bcrypt-version")
        );
        int bcryptStrength = Integer.valueOf(environment.getProperty("bcrypt-strength"));
        return new BCryptPasswordEncoder(bCryptVersion, bcryptStrength);
    }
}
