package com.priyajit.ecommerce.user.management.service.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    private Environment environment;

    @Value("${bcrypt-version}")
    private String BCRYPT_VERISON;

    @Value("${bcrypt-strength}")
    private Integer BCRYPT_STRENGTH;


    public PasswordEncoderConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder.BCryptVersion bCryptVersion = BCryptPasswordEncoder
                .BCryptVersion.valueOf(BCRYPT_VERISON);

        return new BCryptPasswordEncoder(bCryptVersion, BCRYPT_STRENGTH);
    }
}
