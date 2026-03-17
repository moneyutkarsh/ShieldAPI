package com.shieldapi.security.recon;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReconDetectionFilter extends OncePerRequestFilter {

    private final ReconDetectionService reconDetectionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            String ipAddress = getClientIp(request);
            String endpoint = request.getRequestURI();
            int status = responseWrapper.getStatus();

            // We exclude static resources and error paths from recon detection
            if (!isExcludedPath(endpoint)) {
                reconDetectionService.processRequest(ipAddress, endpoint, status);
            }
            
            responseWrapper.copyBodyToResponse();
        }
    }

    private boolean isExcludedPath(String path) {
        return path.startsWith("/static") || path.startsWith("/h2-console") || path.equals("/error");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
