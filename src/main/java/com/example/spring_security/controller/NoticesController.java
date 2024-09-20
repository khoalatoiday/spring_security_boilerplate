package com.example.spring_security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class NoticesController {
    @RequestMapping("/notices")
    public String notices() {
        return "Notices page";
    }
}
