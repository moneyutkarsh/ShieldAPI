package com.shieldapi.config;

import com.shieldapi.monitoring.audit.ApiAuditInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiAuditInterceptor apiAuditInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAuditInterceptor)
                .addPathPatterns("/api/**", "/auth/**"); // Apply audit logging to API and Auth endpoints
    }
}
