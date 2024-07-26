package com.priyajit.ecommerce.orchestratorservice.controller;

import com.priyajit.ecommerce.fs.api.UserManagementServiceApi;
import com.priyajit.ecommerce.fs.model.LoginModel;
import com.priyajit.ecommerce.orchestratorservice.component.CustomObjectMapper;
import com.priyajit.ecommerce.orchestratorservice.component.SecurityContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class UserManagementServiceController implements UserManagementServiceApi {

    private final com.priyajit.ecommerce.ums.api.AuthControllerV1Api authControllerV1Api;
    private final CustomObjectMapper objectMapper;
    private final SecurityContextHelper securityContextHelper;

    public UserManagementServiceController(
            com.priyajit.ecommerce.ums.api.AuthControllerV1Api authControllerV1Api,
            CustomObjectMapper objectMapper,
            SecurityContextHelper securityContextHelper
    ) {
        this.authControllerV1Api = authControllerV1Api;
        this.objectMapper = objectMapper;
        this.securityContextHelper = securityContextHelper;
    }

    /**
     * POST /user-management-service/v1/auth/login
     *
     * @param loginDto (required)
     * @return OK (status code 200)
     * or Bad Request (status code 400)
     */
    @Override
    public Mono<ResponseEntity<com.priyajit.ecommerce.fs.model.LoginModel>> login(
            Mono<com.priyajit.ecommerce.fs.model.LoginDto> loginDto,
            final ServerWebExchange exchange
    ) throws Exception {

        return loginDto
                .doFirst(() -> log.info("[login] Before calling userManagementServiceProxy.loginWithHttpInfo"))
                .flatMap(loginDtoVal -> {
                    var payLoad = objectMapper.map(loginDtoVal, com.priyajit.ecommerce.ums.model.LoginDto.class);
                    return authControllerV1Api.loginWithHttpInfo(payLoad);
                })
                .doOnSuccess((model) -> log.info("[login] After calling userManagementServiceProxy.loginWithHttpInfo"))
                .doOnError((e) -> log.info("[login] After calling userManagementServiceProxy.loginWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(objectMapper.map(response.getBody(), LoginModel.class)));
    }
}
