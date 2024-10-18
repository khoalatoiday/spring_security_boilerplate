package com.example.spring_security.config;

import com.example.spring_security.model.Customer;
import com.example.spring_security.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankUserDetails implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userName, pwd = null;
        List<GrantedAuthority> authorityList = null;
        List<Customer> customers = customerRepository.findByEmail(username);
        if(customers.size() == 0) {
            throw new UsernameNotFoundException("User not found");
        }else {
            userName = customers.get(0).getEmail();
            pwd = customers.get(0).getPwd();
             authorityList = customers.get(0).getAuthorities().stream().map(authority ->
                    new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
        }
        return new User(username, pwd, authorityList);
    }
}
