package com.shieldapi.security.honeypot;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class HoneypotFilter extends OncePerRequestFilter {

    private final HoneypotService honeypotService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();

        if (honeypotService.isHoneypotPath(requestUri)) {
            log.info("Request to honeypot path intercepted: {} from IP: {}", requestUri, remoteAddr);
            
            try {
                honeypotService.handleHoneypotHit(remoteAddr, requestUri, request.getMethod());
            } catch (Exception e) {
                log.error("Error handling honeypot hit for IP {}: {}", remoteAddr, e.getMessage(), e);
            }
            
            // Always return 404 for honeypot paths to avoid revealing the trap
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Not Found");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
