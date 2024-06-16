package com.priyajit.ecommerce.user.management.component.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyajit.ecommerce.user.management.component.UserDetailsParser;
import com.priyajit.ecommerce.user.management.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.user.management.model.UserDetailsModel;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class UserDetailsParserImpl implements UserDetailsParser {

    private DbEnvironmentConfiguration dbEnvironmentConfiguration;

    public UserDetailsParserImpl(DbEnvironmentConfiguration dbEnvironmentConfiguration) {
        this.dbEnvironmentConfiguration = dbEnvironmentConfiguration;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class JwtClaimsDto {
        private String sub;
        private Long iat;
        private Long exp;
        private List<String> roles;
    }

    @Override
    public UserDetailsModel getFromToken(String token) {

        var jwtParser = Jwts.parser();
        var signingKey = Base64.getEncoder()
                .encode(dbEnvironmentConfiguration.getProperty(DbEnvironmentConfiguration.Keys.JWT_SECRET_KEY)
                        .getBytes(StandardCharsets.UTF_8));
        jwtParser.setSigningKey(signingKey);
        Jwt jwt;
        try {
            jwt = jwtParser.parse(token);
        } catch (SignatureException e) {
            log.error("Error while parsing JWT");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided token is invalid");
        }

        var objectMapper = new ObjectMapper();
        var claimsDto = objectMapper.convertValue(jwt.getBody(), JwtClaimsDto.class);

        var validatedAt = new Date().getTime() / 1000; // in seconds
        if (claimsDto.getExp() < validatedAt) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided token is expired");
        }

        var model = UserDetailsModel.builder()
                .userId(claimsDto.getSub())
                .roles(claimsDto.getRoles())
                .build();

        return model;
    }
}
