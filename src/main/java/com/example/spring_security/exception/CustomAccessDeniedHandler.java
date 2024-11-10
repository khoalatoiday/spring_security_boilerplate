package com.example.spring_security.exception;

import com.example.spring_security.DTO.Exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setHeader("WWW-Authenticate", "Authorization failed");
        response.setContentType("application/json;charset=UTF-8");
        Exception exception = new Exception(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                accessDeniedException.getMessage(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                request.getRequestURI()
        );
        response.setStatus(HttpStatus.FORBIDDEN.value());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(objectMapper.writeValueAsString(exception));
    }
}
