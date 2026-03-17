package com.shieldapi.monitoring.audit;

import com.shieldapi.monitoring.metrics.SecurityMetricsService;
import com.shieldapi.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiAuditInterceptor implements HandlerInterceptor {

    private final ApiAuditService apiAuditService;
    private final SecurityMetricsService securityMetricsService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
                             @NonNull HttpServletResponse response, 
                             @NonNull Object handler) {
        securityMetricsService.incrementRequestCount();
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, 
                                @NonNull HttpServletResponse response, 
                                @NonNull Object handler, 
                                Exception ex) {
        
        Long startTime = (Long) request.getAttribute("startTime");
        long responseTime = (startTime != null) ? (System.currentTimeMillis() - startTime) : 0;

        String ipAddress = getClientIp(request);
        String userId = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            userId = String.valueOf(user.getId());
        }

        ApiAuditLog auditLog = ApiAuditLog.builder()
                .ipAddress(ipAddress)
                .requestUri(request.getRequestURI())
                .httpMethod(request.getMethod())
                .responseStatus(response.getStatus())
                .responseTime(responseTime)
                .userId(userId != null ? Long.parseLong(userId) : null)
                .timestamp(LocalDateTime.now())
                .build();

        apiAuditService.saveAuditLog(auditLog);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
