package com.priyajit.ecommerce.orchestratorservice.config;

import com.priyajit.ecommerce.orchestratorservice.config.properties.UserTokenParserProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    private final static String[] UNAUTHENTICATED_ENDPOINTS = {
            "/swagger-ui/*", "/v3/api-docs", "/v3/api-docs/swagger-config",
            "/user-management-service/v1/auth/login",
            "/product-catalog-service/v1/product/search"
    };

    private UserTokenParserProperties userTokenParserProperties;

    public WebSecurityConfig(UserTokenParserProperties userTokenParserProperties) {
        this.userTokenParserProperties = userTokenParserProperties;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity) throws Exception {
        log.info("Configuring securityFilterChain");
        httpSecurity.csrf(csrfSpec -> csrfSpec.disable());
        httpSecurity.cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()));

        httpSecurity.authorizeExchange(customizer -> customizer
                .pathMatchers(UNAUTHENTICATED_ENDPOINTS).permitAll()
                .anyExchange().authenticated());

        // JWT
        byte secretKey[] = Base64.getEncoder().encode(userTokenParserProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        httpSecurity.oauth2ResourceServer(customizer -> customizer
                .jwt(jwtCustomizer -> jwtCustomizer
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        .jwtDecoder(reactiveJwtDecoder(secretKey, userTokenParserProperties.getAlgorithm())))
        );

        return httpSecurity.build();

    }

    private Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return (jwt) -> {
            var claims = jwt.getClaims();
            String subject = (String) claims.get("sub");
            List<String> roles = (List<String>) claims.get("roles");
            List<GrantedAuthority> grantedAuthorities = roles.stream().map(this::roleToGrantedAuthority).collect(Collectors.toList());
            var authToken = new JwtAuthenticationToken(jwt, grantedAuthorities, subject);
            return Mono.just(authToken);
        };
    }

    /**
     * Helper method to create ReactiveJwtDecoder
     *
     * @param key
     * @return
     */
    private ReactiveJwtDecoder reactiveJwtDecoder(byte[] key, String algorithm) {
        SecretKey secretKey = new SecretKeySpec(key, algorithm);
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Helper method maps a role:String to GrantedAuthority
     *
     * @param role
     * @return
     */
    private GrantedAuthority roleToGrantedAuthority(String role) {
        return () -> role;
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return exchange -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(List.of("*"));
            configuration.setAllowedOrigins(List.of("localhost:3000"));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("Access-Control-Allow-Origin", "Authorization", "Content-Type"));
            configuration.setAllowCredentials(true);
            return configuration;
        };
    }
}
