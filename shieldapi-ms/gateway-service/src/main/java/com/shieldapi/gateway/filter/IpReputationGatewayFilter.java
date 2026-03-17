package com.shieldapi.gateway.filter;

import com.shieldapi.gateway.service.DynamicBlacklistService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class IpReputationGatewayFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;
    private final DynamicBlacklistService blacklistService;
    private final MeterRegistry meterRegistry;

    public IpReputationGatewayFilter(WebClient.Builder webClientBuilder, DynamicBlacklistService blacklistService, MeterRegistry meterRegistry) {
        this.webClient = webClientBuilder.baseUrl("http://security-service").build();
        this.blacklistService = blacklistService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getAttribute(IpTrackingFilter.CLIENT_IP_ATTR);
        if (clientIp == null) {
            clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        final String ip = clientIp;

        // 1. Fast Path: Check local dynamic blacklist
        if (blacklistService.isBlacklisted(ip)) {
            log.warn("Fast-Path Block: IP {} is in the local dynamic blacklist", ip);
            meterRegistry.counter("shieldapi.requests.blocked", "type", "fast-path").increment();
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // 2. Slow Path: Call security-service for long-term IP reputation
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/security/check-ip").queryParam("ip", ip).build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    Boolean isBlacklisted = (Boolean) response.get("blacklisted");
                    if (isBlacklisted != null && isBlacklisted) {
                        log.warn("Persistent Block: IP {} is blacklisted in security-service", ip);
                        meterRegistry.counter("shieldapi.requests.blocked", "type", "slow-path").increment();
                        // Add to local cache for 5 minutes to avoid frequent network calls
                        blacklistService.blacklistIp(ip, 5);
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    log.error("Failed to check IP reputation, allowing request: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -100; // Run early
    }
}
