package com.priyajit.ecommerce.orchestratorservice.component.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestLogginFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.empty()
                .doFirst(() -> log.info("{} {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getPath(), exchange.getRequest().getId()))
                .then(chain.filter(exchange));
    }
}
