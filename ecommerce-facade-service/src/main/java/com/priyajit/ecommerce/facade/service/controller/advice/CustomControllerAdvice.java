package com.priyajit.ecommerce.facade.service.controller.advice;

import com.priyajit.ecommerce.facade.service.model.ErrorResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ControllerAdvice
public class CustomControllerAdvice {

    private final static String logPattern = "{} occurred, resolving with status: {}, error: {}, cause: {}";

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponseModel> handleWebClientResponseException(WebClientResponseException throwable) {
        var status = throwable.getStatusCode();
        log.error(logPattern, WebClientResponseException.class.getSimpleName(), status, throwable.getMessage(), throwable.getCause(), throwable);

        var error = status.is5xxServerError()
                ? "A server side error occurred in a downstream service"
                : throwable.getResponseBodyAs(ErrorResponseModel.class).getError();
        var model = ErrorResponseModel
                .builder()
                .timestamp(now())
                .status(String.valueOf(status.value()))
                .error(error)
                .build();
        return ResponseEntity.status(status).body(model);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseModel> handleResponseStatusException(ResponseStatusException throwable) {
        var status = throwable.getStatusCode();
        log.error(logPattern, WebClientResponseException.class.getSimpleName(), status, throwable.getMessage(), throwable.getCause(), throwable);

        var error = status.is5xxServerError()
                ? "An error occurred"
                : throwable.getReason();
        var model = ErrorResponseModel
                .builder()
                .timestamp(now())
                .status(String.valueOf(status.value()))
                .error(error)
                .build();
        return ResponseEntity.status(status).body(model);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponseModel> handleConnectException(ConnectException throwable) {
        var status = HttpStatus.SERVICE_UNAVAILABLE;
        log.error(logPattern, WebClientResponseException.class.getSimpleName(), status, throwable.getMessage(), throwable.getCause(), throwable);

        var model = ErrorResponseModel
                .builder()
                .timestamp(now())
                .status(String.valueOf(status.value()))
                .error("Unable to connect to downstream service")
                .build();
        return ResponseEntity.status(status).body(model);
    }

    private String now() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}
