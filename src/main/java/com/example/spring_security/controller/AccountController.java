package com.example.spring_security.controller;

import com.example.spring_security.model.Accounts;
import com.example.spring_security.model.Customer;
import com.example.spring_security.repository.AccountsRepository;
import com.example.spring_security.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountsRepository accountsRepository;

    private final CustomerRepository customerRepository;

    @GetMapping("/myAccount")
    public Accounts getAccountDetails(@RequestParam long id) {
        Accounts accounts = accountsRepository.findByCustomerId(id);
        if (accounts != null) {
            return accounts;
        } else {
            return null;
        }
    }

    @GetMapping("/myCustomer")
    public Customer getCustomer(@RequestParam String email) {
        List<Customer> customers = customerRepository.findByEmail(email);
        if (customers != null) {
            return customers.get(0);
        } else {
            return null;
        }
    }

}
