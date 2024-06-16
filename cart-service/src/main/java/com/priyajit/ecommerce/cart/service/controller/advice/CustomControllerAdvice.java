package com.priyajit.ecommerce.cart.service.controller.advice;

import com.priyajit.ecommerce.cart.service.model.ErrorResponseModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseModel> handleResponseStatusException(
            ResponseStatusException throwable, HttpServletRequest request, HttpServletResponse response
    ) {
        var responseBuilder = ErrorResponseModel
                .builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .requestId(response.getHeader("request-id"))
                .path(request.getServletPath())
                .status(String.valueOf(throwable.getStatusCode().value()))
                .error(throwable.getReason());

        return ResponseEntity.status(throwable.getStatusCode()).body(responseBuilder.build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseModel> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException throwable, HttpServletRequest request, HttpServletResponse response
    ) {
        var message = throwable.getFieldErrors().stream().map(error -> error.getField() + " " + error.getDefaultMessage()).reduce(" ", String::concat);
        var responseBuilder = ErrorResponseModel
                .builder()
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .requestId(response.getHeader("request-id"))
                .path(request.getServletPath())
                .status(String.valueOf(throwable.getStatusCode().value()))
                .error(message);


        return ResponseEntity.status(throwable.getStatusCode()).body(responseBuilder.build());
    }
}
