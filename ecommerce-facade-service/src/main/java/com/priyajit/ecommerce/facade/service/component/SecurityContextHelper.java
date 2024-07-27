package com.priyajit.ecommerce.facade.service.component;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface SecurityContextHelper {
    Mono<String> getUserId();
}
