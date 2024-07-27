package com.priyajit.ecommerce.facade.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class RequestSchema implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("key")
    String key;

    public RequestSchema(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
