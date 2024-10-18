package com.example.spring_security.component;

import com.example.spring_security.model.Customer;
import com.example.spring_security.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String presentedPassword = authentication.getCredentials().toString();
        List<Customer> customers = customerRepository.findByEmail(username);
        if(customers.size() == 0) {
            throw new UsernameNotFoundException("User not found");
        }
        String pwd = customers.get(0).getPwd();
        List<GrantedAuthority>  authorityList = customers.get(0).getAuthorities().stream().map(authority ->
                new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
        if (!this.passwordEncoder.matches(presentedPassword, pwd)) {
            throw new BadCredentialsException("Bad credentials");
        }
        return new UsernamePasswordAuthenticationToken(username, pwd, authorityList);
    }

    @Override
    public boolean supports(Class<?> authentication) {
         return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
