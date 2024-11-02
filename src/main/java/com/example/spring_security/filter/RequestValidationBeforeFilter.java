package com.example.spring_security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class RequestValidationBeforeFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if(null != header) {
            header = header.trim();
            if(StringUtils.startsWithIgnoreCase(header, "Basic ")) {
                byte[] byte64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
                byte[] decoded = null;

                try {
                    decoded = Base64.getDecoder().decode(byte64Token);
                    String token = new String(decoded, StandardCharsets.UTF_8);
                    int delim = token.indexOf(":");
                    if(delim == -1) {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                    String email = token.substring(0, delim);
                    log.info(email);
                }catch (Exception e) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        chain.doFilter(request, response);
    }
}
