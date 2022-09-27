package com.example.weatherapplicationrestapi.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.annotation.security.DenyAll;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMsgDTO {
    private String error;
}
