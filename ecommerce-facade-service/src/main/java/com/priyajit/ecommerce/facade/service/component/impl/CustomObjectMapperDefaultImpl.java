package com.priyajit.ecommerce.facade.service.component.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.priyajit.ecommerce.facade.service.component.CustomObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomObjectMapperDefaultImpl implements CustomObjectMapper {
    private ObjectMapper objectMapper;

    public CustomObjectMapperDefaultImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Maps object to required type
     *
     * @param src
     * @param targetType
     * @param <T>
     * @return
     */
    @Override
    public <T> T map(Object src, Class<T> targetType) {
        return objectMapper.convertValue(src, targetType);
    }

    /**
     * Maps object to required type
     *
     * @param src
     * @param targetType
     * @param <T>
     * @return
     */
    @Override
    public <T> T map(Object src, TypeReference<T> targetType) {
        return objectMapper.convertValue(src, targetType);
    }
}
