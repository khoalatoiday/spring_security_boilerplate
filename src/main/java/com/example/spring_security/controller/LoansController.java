package com.example.spring_security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LoansController {
    @RequestMapping("/loans")
    public String loans() {
        return "Loans page";
    }
}
