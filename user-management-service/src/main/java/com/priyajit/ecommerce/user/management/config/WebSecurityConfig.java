package com.priyajit.ecommerce.user.management.config;


import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, DbEnvironmentConfiguration configuration) throws Exception {
        // cors
        httpSecurity.cors(cors -> cors.disable());
        // csrf
        httpSecurity.csrf(csrf -> csrf.disable());

        // authenticate endpoints
        httpSecurity.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/v1/auth/login", "/swagger-ui/index.html#/*").permitAll()
                .anyRequest().authenticated()
        );

        // JWT
        byte secretKey[] = Base64.getEncoder().encode(configuration.getProperty(DbEnvironmentConfiguration.Keys.JWT_SECRET_KEY).getBytes(StandardCharsets.UTF_8));
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                .decoder(jwtDecoder(secretKey)))
        );

        return httpSecurity.build();

    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return (jwt) -> {
            var claims = jwt.getClaims();
            String subject = (String) claims.get("sub");
            var authToken = new JwtAuthenticationToken(jwt, List.of(() -> "DEFAULT_GRANTED_AUTHORITY"), subject);
            return authToken;
        };
    }

    private JwtDecoder jwtDecoder(byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
