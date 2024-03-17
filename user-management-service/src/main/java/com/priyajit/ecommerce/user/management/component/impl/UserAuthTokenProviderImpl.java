package com.priyajit.ecommerce.user.management.component.impl;

import com.priyajit.ecommerce.user.management.component.UserAuthTokenProvider;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration.Keys;

@Component
public class UserAuthTokenProviderImpl implements UserAuthTokenProvider {

    private DbEnvironmentConfiguration dbEnvironmentConfiguration;

    public UserAuthTokenProviderImpl(DbEnvironmentConfiguration dbEnvironmentConfiguration) {
        this.dbEnvironmentConfiguration = dbEnvironmentConfiguration;
    }

    @Override
    public String generateToken(String sub) {

        long validity = Long.parseLong(dbEnvironmentConfiguration.getProperty(Keys.JWT_VALIDITY_MILLIS));
        Date issueAt = new Date();
        Date expiration = new Date(issueAt.getTime() + validity);

        Claims claims = Jwts.claims().setSubject(sub);

        byte[] secretKey = Base64.getEncoder().encode(dbEnvironmentConfiguration.getProperty(Keys.JWT_SECRET_KEY).getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issueAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.forName("HS256"), secretKey)
                .compact();
    }
}
