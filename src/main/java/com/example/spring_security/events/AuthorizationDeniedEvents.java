package com.example.spring_security.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizationDeniedEvents {

    @EventListener
    public void onFailure(AuthorizationDeniedEvent failureEvent) {
        log.error("Authorization failure:", failureEvent.getAuthentication().get().getAuthorities());
    }
}
