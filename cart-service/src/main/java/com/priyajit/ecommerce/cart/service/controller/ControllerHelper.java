package com.priyajit.ecommerce.cart.service.controller;


import com.priyajit.ecommerce.cart.service.model.Response;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
