package com.shieldapi.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/security")
    public Mono<String> securityFallback() {
        return Mono.just("Security Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/analytics")
    public Mono<String> analyticsFallback() {
        return Mono.just("Analytics Service is currently unavailable. Data may be delayed.");
    }
}
