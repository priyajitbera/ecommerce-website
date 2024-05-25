package com.priyajit.ecommerce.product.catalog.service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {


    /**
     * Logs incoming requests
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestId = generateRequestID();
        log.info("{} {} {}", requestId, request.getMethod(), request.getRequestURI());
        response.setHeader("request-id", requestId);
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method generates a random requestId
     *
     * @return
     */
    private String generateRequestID() {
        return UUID.randomUUID().toString().split("-")[4];
    }
}
