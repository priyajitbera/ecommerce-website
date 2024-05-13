package com.priyajit.ecommerce.cart.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response<T> {
    private T data;
    private String error;
}
