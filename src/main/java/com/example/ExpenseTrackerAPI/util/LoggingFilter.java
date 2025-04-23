package com.example.ExpenseTrackerAPI.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String user = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName() : "Anonymous";
        log.info("Request: Method={}, URI={}, User={}", request.getMethod(), request.getRequestURI(), user);
        filterChain.doFilter(request, response);
    }

}
