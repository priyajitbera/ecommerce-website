package com.priyajit.ecommerce.product.catalog.service.controller.advice;

import com.priyajit.ecommerce.product.catalog.service.model.ErrorResponseModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ConnectionClosedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ControllerAdvice
public class CustomControllerAdvice {

    private final static String logPattern = "{} occurred, resolving with status: {}, error: {}";

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseModel> handleResponseStatusException(
            ResponseStatusException throwable, HttpServletRequest request, HttpServletResponse response
    ) {
        var status = throwable.getStatusCode();
        log.error(logPattern, ResponseStatusException.class.getSimpleName(), status, throwable.getMessage(), throwable);

        var model = ErrorResponseModel
                .builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .requestId(response.getHeader("request-id"))
                .path(request.getServletPath())
                .status(String.valueOf(status.value()))
                .error(throwable.getReason())
                .build();

        return ResponseEntity.status(status).body(model);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseModel> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException throwable, HttpServletRequest request, HttpServletResponse response
    ) {
        var status = throwable.getStatusCode();
        log.error(logPattern, MethodArgumentNotValidException.class.getSimpleName(), status, throwable.getMessage(), throwable);

        var message = throwable.getFieldErrors().stream().map(error -> error.getField() + " " + error.getDefaultMessage()).reduce(" ", String::concat);
        var responseBuilder = ErrorResponseModel
                .builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .requestId(response.getHeader("request-id"))
                .path(request.getServletPath())
                .status(String.valueOf(status.value()))
                .error(message);


        return ResponseEntity.status(throwable.getStatusCode()).body(responseBuilder.build());
    }

    @ExceptionHandler({ConnectException.class, ConnectionClosedException.class})
    public ResponseEntity<ErrorResponseModel> handleConnectException(Throwable throwable) {
        var status = HttpStatus.SERVICE_UNAVAILABLE;
        log.error(logPattern, ConnectException.class.getSimpleName(), status, throwable.getMessage(), throwable);

        var model = ErrorResponseModel.builder()
                .status(String.valueOf(status.value()))
                .error(throwable.getMessage())
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .build();
        return ResponseEntity.status(status).body(model);
    }
}
