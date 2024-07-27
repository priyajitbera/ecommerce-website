package com.priyajit.ecommerce.product.catalog.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ElasticSearchConnectionException extends ResponseStatusException {
    private final static HttpStatusCode httpStatusCode = HttpStatus.SERVICE_UNAVAILABLE;

    public ElasticSearchConnectionException() {
        super(httpStatusCode);
    }

    public ElasticSearchConnectionException(String reason) {
        super(httpStatusCode, reason);
    }

    public ElasticSearchConnectionException(String reason, Throwable cause) {
        super(httpStatusCode, reason, cause);
    }
}
