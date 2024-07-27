package com.priyajit.example.ecommercegateway.exception.handler;

import com.priyajit.example.ecommercegateway.model.ErrorResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponseModel> handleConnectException(ConnectException throwable) {

        log.error("{} occurred, error: {}, cause: {}",
                throwable.getClass().getSimpleName(), throwable.getMessage(), throwable.getCause(), throwable);

        var status = HttpStatus.SERVICE_UNAVAILABLE;
        var model = ErrorResponseModel.builder()
                .status(String.valueOf(status.value()))
                .error(throwable.getMessage())
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .build();
        return ResponseEntity.status(status).body(model);
    }
}
