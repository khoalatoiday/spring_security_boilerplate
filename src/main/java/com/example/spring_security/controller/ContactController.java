package com.example.spring_security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ContactController {
    @RequestMapping("/contact")
    public String contact() {
        return "Contact page";
    }
}
