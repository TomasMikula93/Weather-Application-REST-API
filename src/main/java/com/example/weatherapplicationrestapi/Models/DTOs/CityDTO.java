package com.example.weatherapplicationrestapi.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CityDTO {

    private String name;
    private double longitude;
    private double latitude;
}
