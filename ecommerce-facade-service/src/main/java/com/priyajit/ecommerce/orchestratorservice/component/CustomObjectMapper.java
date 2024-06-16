package com.priyajit.ecommerce.orchestratorservice.component;

import com.fasterxml.jackson.core.type.TypeReference;

public interface CustomObjectMapper {

    <T> T map(Object src, Class<T> targetType);

    <T> T map(Object src, TypeReference<T> targetType);
}
