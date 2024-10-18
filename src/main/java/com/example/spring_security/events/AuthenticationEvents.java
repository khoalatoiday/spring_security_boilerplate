package com.example.spring_security.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.info("Authentication success: {} at {}", successEvent.getAuthentication().getName(), successEvent.getTimestamp());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        log.error("Authentication failure: {} at {}. Due to {}",
                failureEvent.getAuthentication().getName(),
                failureEvent.getTimestamp(),
                failureEvent.getException().getMessage()
                );
    }
}
