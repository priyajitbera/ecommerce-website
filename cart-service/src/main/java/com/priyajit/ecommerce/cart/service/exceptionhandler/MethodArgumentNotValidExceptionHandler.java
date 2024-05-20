package com.priyajit.ecommerce.cart.service.exceptionhandler;

import com.priyajit.ecommerce.cart.service.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface MethodArgumentNotValidExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    default Response<Void> handle(
            MethodArgumentNotValidException e
    ) {
        var message = e.getFieldError().getField() + " " + e.getFieldError().getDefaultMessage();
        return Response.<Void>builder().error(message).build();
    }
}
