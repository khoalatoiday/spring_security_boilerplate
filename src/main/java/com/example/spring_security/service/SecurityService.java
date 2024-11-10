package com.example.spring_security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("securityService")
@Slf4j
public class SecurityService {
    public boolean hasPermission(String permission) {
        log.info("Checking permission: " + permission);
        return true;
    }
}
