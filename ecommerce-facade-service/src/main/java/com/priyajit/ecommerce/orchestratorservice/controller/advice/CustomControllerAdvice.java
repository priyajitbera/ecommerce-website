package com.priyajit.ecommerce.orchestratorservice.controller.advice;

import com.priyajit.ecommerce.orchestratorservice.model.ErrorResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponseModel> handleWebClientResponseException(WebClientResponseException throwable) {
        throwable.printStackTrace();
        var responseBuilder = ErrorResponseModel
                .builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        responseBuilder.status(String.valueOf(throwable.getStatusCode().value()));
        responseBuilder.error(throwable.getResponseBodyAs(ErrorResponseModel.class).getError());
        return ResponseEntity.status(throwable.getStatusCode()).body(responseBuilder.build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseModel> handleResponseStatusException(ResponseStatusException throwable) {
        throwable.printStackTrace();
        var responseBuilder = ErrorResponseModel
                .builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        responseBuilder.status(String.valueOf(throwable.getStatusCode().value()));
        responseBuilder.error(throwable.getReason());
        return ResponseEntity.status(throwable.getStatusCode()).body(responseBuilder.build());
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponseModel> handleConnectException(ConnectException throwable) {
        throwable.printStackTrace();
        var responseBuilder = ErrorResponseModel
                .builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        var httpStatus = HttpStatus.BAD_GATEWAY;
        responseBuilder.status(String.valueOf(httpStatus.value()));
        responseBuilder.error("Unable to connect to downstream service");
        return ResponseEntity.status(httpStatus).body(responseBuilder.build());
    }
}
