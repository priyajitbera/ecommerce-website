package com.priyajit.ecommerce.orchestratorservice.component;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface SecurityContextHelper {
    Mono<String> getUserId();
}
