package com.example.spring_security.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Exception implements Serializable {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
