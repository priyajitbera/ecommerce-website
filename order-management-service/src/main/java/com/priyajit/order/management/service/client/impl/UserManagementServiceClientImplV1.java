package com.priyajit.order.management.service.client.impl;

import com.priyajit.order.management.service.client.UserManagementServiceClient;
import com.priyajit.order.management.service.client.model.UserModel;
import com.priyajit.order.management.service.exception.NullArgumentException;
import com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static com.priyajit.order.management.service.mongodoc.DbEnvironmentConfiguration.Keys;

@Slf4j
@Component
public class UserManagementServiceClientImplV1 implements UserManagementServiceClient {

    private DbEnvironmentConfiguration configuration;

    public UserManagementServiceClientImplV1(DbEnvironmentConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Optional<UserModel> findUserByUserId(String userId) {
        if (userId == null) throw new NullArgumentException("userId", String.class);

        var BASE_URL = configuration.getProperty(Keys.USER_MANAGEMENT_SERVICE_BASE_URL);
        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();

        ResponseEntity<UserModel> response;
        try {
            log.info("Before calling user-management-service API");
            response = restClient.get().uri(uri -> uri
                            .path("/v1/user/find-one")
                            .queryParam("userId", userId)
                            .build())
                    .retrieve()
                    .toEntity(UserModel.class);
            log.info("After calling user-management-service API, status:{}", response.getStatusCode());
        }
        // error
        catch (HttpClientErrorException e) {
            log.info("After calling user-management-service API, status:{}", e.getStatusCode());
            if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
                return Optional.empty();
            } else {
                throw new RuntimeException(String.format("Error occurred while calling product-catalog-service API, status:%s, %s",
                        e.getStatusCode(), e.getMessage(), e));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error occurred while calling product-catalog-service API, %s",
                    e.getMessage(), e));
        }
        if (HttpStatus.OK == response.getStatusCode()) {
            var productModel = response.getBody();
            // validate response body content
            if (productModel == null) {
                return Optional.empty();
            }
            return Optional.of(productModel);
        } else if (HttpStatus.NOT_FOUND == response.getStatusCode()) {
            return Optional.empty();
        } else
            throw new RuntimeException(String.format("Error occurred while calling product-catalog-service API, status: %s",
                    response.getStatusCode()));

    }
}
