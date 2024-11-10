package com.example.spring_security.controller;

import com.example.spring_security.model.Accounts;
import com.example.spring_security.model.Customer;
import com.example.spring_security.repository.AccountsRepository;
import com.example.spring_security.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private  AccountsRepository accountsRepository;

    @Autowired
    private  CustomerRepository customerRepository;

    @GetMapping("/myAccount")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostAuthorize("@securityService.hasPermission('USER')")
    public ResponseEntity<Accounts> getAccountDetails(@RequestParam long id) {
        Accounts accounts = accountsRepository.findByCustomerId(id);
        if (accounts != null) {
            // PostAuthorize logic sẽ gọi trước khi lệnh return được gọi
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } else {
            // PostAuthorize logic sẽ gọi trước khi lệnh return được gọi
            return null;
        }
    }

    @PreFilter("filterObject.customerId  != 1") // input chỉ lấy những element mà có customerId != 1
    @PostFilter("filterObject.customerId  != 1") // output chỉ trả về những element mà có customerId != 1
    @PostMapping("/listAccounts")
    public ResponseEntity<List<Accounts>> getByIds(@RequestBody List<Accounts> accounts) {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
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
