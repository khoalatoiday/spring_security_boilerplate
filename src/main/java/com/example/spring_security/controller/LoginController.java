package com.example.spring_security.controller;


import com.example.spring_security.DTO.LoginRequestDto;
import com.example.spring_security.DTO.LoginResponseDto;
import com.example.spring_security.component.CustomAuthenticationProvider;
import com.example.spring_security.model.Customer;
import com.example.spring_security.repository.CustomerRepository;
import com.example.spring_security.utils.Constants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/oauth")
public class LoginController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // authentication provider
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private  Environment env;




    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Customer customer) {
        try {
            String hashPwd = passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashPwd);
            customer.setCreateDt(new Date(System.currentTimeMillis()));
            Customer savedCustomer = customerRepository.save(customer);

            if (savedCustomer.getId() > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).
                        body("Given user details are successfully registered");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("User registration failed");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("An exception occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/current")
    public ResponseEntity<String> current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        return new ResponseEntity<>(userName, HttpStatus.OK);
    }

    @PostMapping("/logincustom")
    public ResponseEntity<LoginResponseDto> loginCustom(@RequestBody LoginRequestDto form) {
        String jwt = "";
        LoginResponseDto res = new LoginResponseDto();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                UsernamePasswordAuthenticationToken.unauthenticated(form.getUserName(), form.getPassword());
        // authentication manager sẽ gọi từng hàm authenticate của list authenticationProvider cho đến khi nào có một provider trả về authentication
        Authentication authentication = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if(null != authentication && authentication.isAuthenticated()) {
            String secret = env.getProperty(Constants.SECRET_KEY, Constants.SECRET_KEY_DEFAULT_VAL);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
             jwt = Jwts.builder().issuer("Spring Security Boilerplate")
                    .setSubject(authentication.getName())
                    .claims()
                    .add("username", authentication.getName())
                    .add("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .and()
                    .issuedAt(new java.util.Date())
                    .expiration(new java.util.Date(new java.util.Date().getTime() + 1000 * 60 * 5000))
                    .signWith(secretKey)
                    .compact();
            res.setToken(jwt);
            res.setExpireDate(new java.util.Date(new java.util.Date().getTime() + 1000 * 60 * 5000));
            res.setStatus(HttpStatus.OK.getReasonPhrase());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        res.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}
