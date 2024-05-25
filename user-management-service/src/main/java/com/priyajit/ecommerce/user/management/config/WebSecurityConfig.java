package com.priyajit.ecommerce.user.management.config;


import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.stream.Collectors;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final static String[] UNAUTHENTICATED_ENDPOINTS = {
            "/v1/auth/login", "/swagger-ui/*", "/v3/api-docs", "/v3/api-docs/swagger-config"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, DbEnvironmentConfiguration configuration) throws Exception {
        // cors
        httpSecurity.cors(cors -> cors.disable());
        // csrf
        httpSecurity.csrf(csrf -> csrf.disable());

        // authenticate endpoints
        httpSecurity.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(UNAUTHENTICATED_ENDPOINTS).permitAll()
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
            List<String> roles = (List<String>) claims.get("roles");
            List<GrantedAuthority> grantedAuthorities = roles.stream().map(this::roleToGrantedAuthority).collect(Collectors.toList());
            var authToken = new JwtAuthenticationToken(jwt, grantedAuthorities, subject);
            return authToken;
        };
    }

    private JwtDecoder jwtDecoder(byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Helper method creates map a role to GrantedAuthority
     *
     * @param role
     * @return
     */
    private GrantedAuthority roleToGrantedAuthority(String role) {
        return () -> role;
    }
}
