package com.example.spring_security.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto {
    private String status;
    private String token;
    private Date expireDate;
}
