package com.shieldapi.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Incoming Request: Method={}, URI={}, Path={}, RemoteAddress={}",
                request.getMethod(),
                request.getURI(),
                request.getPath(),
                request.getRemoteAddress());
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200; // Run very early
    }
}
