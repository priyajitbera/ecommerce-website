package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.model.Response;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class ControllerHelper {

    /**
     * Wraps the supplied value of type T with as ResponseEntity<Response<T>>
     * and logs error
     *
     * @param supplier
     * @param log
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<Response<T>> supplyResponse(Supplier<T> supplier, Logger log) {
        try {
            var model = supplier.get();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<T>builder().data(model).build());
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            // for ResponseStatusException provide the error reason in response
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<T>builder().error(e.getReason()).build());
        } catch (Throwable e) {
            log.error(e.getMessage());
            e.printStackTrace();
            // for Unknown error do not provide the error reason in response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<T>builder().build());
        }
    }

    public static String getUserId(Authentication auth) {
        if (auth.getPrincipal().getClass().equals(Jwt.class)) {
            return (String) ((Jwt) auth.getPrincipal()).getClaims().get("sub");
        } else {
            throw new RuntimeException("Unknown authentication principle");
        }
    }
}
