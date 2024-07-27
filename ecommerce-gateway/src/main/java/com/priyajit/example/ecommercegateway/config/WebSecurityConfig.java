package com.priyajit.example.ecommercegateway.config;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private AuthTokenProperties authTokenProperties;

    public WebSecurityConfig(AuthTokenProperties authTokenProperties) {
        this.authTokenProperties = authTokenProperties;
    }

    @Bean
    public SecurityWebFilterChain authorizationServerSecurityFilterChain(ServerHttpSecurity http)
            throws Exception {
        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new BearerTokenServerAuthenticationEntryPoint()
                        )
                )
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));


        http.authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec.anyExchange().authenticated())
                .authenticationManager(new JwtReactiveAuthenticationManager(jwtDecoder()));

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        try {
            var rawKey = loadPublicKeyAsString()
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "");
            byte[] decoded = Base64.decode(rawKey);
            RSAPublicKey key = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));
            return NimbusReactiveJwtDecoder.withPublicKey(key).build();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String loadPublicKeyAsString() {
        try {
            return Files.readString(Path.of(authTokenProperties.getPublicKeyFilePath()));
        } catch (Throwable e) {
            log.error("Error occurred while loading publicKey, error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
