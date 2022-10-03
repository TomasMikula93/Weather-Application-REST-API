package com.example.weatherapplicationrestapi.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeatherDTO {
    private String country;
    private String name;
    private String temperature;
}
