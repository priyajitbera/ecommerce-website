package com.priyajit.ecommerce.orchestratorservice.controller;

import com.priyajit.ecommerce.orchestrator_service.api.UserManagementServiceApi;
import com.priyajit.ecommerce.orchestrator_service.model.LoginDto;
import com.priyajit.ecommerce.orchestrator_service.model.LoginModel;
import com.priyajit.ecommerce.orchestratorservice.component.CustomObjectMapper;
import com.priyajit.ecommerce.orchestratorservice.component.SecurityContextHelper;
import com.priyajit.ecommerce.user_management_service.api.AuthControllerV1Api;
import com.priyajit.ecommerce.user_management_service.api.UserControllerV1Api;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class UserManagementServiceController implements UserManagementServiceApi {

    private UserControllerV1Api userControllerV1Api;
    private AuthControllerV1Api authControllerV1Api;
    private CustomObjectMapper objectMapper;
    private SecurityContextHelper securityContextHelper;

    public UserManagementServiceController(
            UserControllerV1Api userControllerV1Api,
            AuthControllerV1Api authControllerV1Api,
            CustomObjectMapper objectMapper,
            SecurityContextHelper securityContextHelper
    ) {
        this.userControllerV1Api = userControllerV1Api;
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
    public Mono<ResponseEntity<LoginModel>> login(
            @Valid @RequestBody Mono<LoginDto> loginDto,
            final ServerWebExchange exchange
    ) throws Exception {

        return Mono.empty()
                .then(loginDto)
                .doFirst(() -> log.info("Before calling authControllerV1Api.loginWithHttpInfo"))
                .flatMap(loginDtoVal -> {
                    var payLoad = objectMapper.map(loginDtoVal, com.priyajit.ecommerce.user_management_service.model.LoginDto.class);
                    return authControllerV1Api.loginWithHttpInfo(payLoad);
                })
                .doOnSuccess((model) -> log.info("After calling authControllerV1Api.loginWithHttpInfo"))
                .doOnError((e) -> log.info("After calling authControllerV1Api.loginWithHttpInfo error occurred, {}", e.getMessage()))
                .map(response -> ResponseEntity.status(response.getStatusCode()).body(objectMapper.map(response.getBody(), LoginModel.class)));
    }
}
