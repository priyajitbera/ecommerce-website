package com.priyajit.ecommerce.user.management.service.service.config;

import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    private String bcryptVersion;

    private Integer bcryptStrength;


    public PasswordEncoderConfig(DbEnvironmentConfiguration dbEnvConfig) {

        this.bcryptVersion = dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.BCRYPT_VERSION);
        this.bcryptStrength = Integer.valueOf(
                dbEnvConfig.getProperty(DbEnvironmentConfiguration.Keys.BCRYPT_STRENGTH)
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {

        BCryptPasswordEncoder.BCryptVersion bCryptVersion = BCryptPasswordEncoder
                .BCryptVersion.valueOf(bcryptVersion);

        return new BCryptPasswordEncoder(bCryptVersion, bcryptStrength);
    }
}
