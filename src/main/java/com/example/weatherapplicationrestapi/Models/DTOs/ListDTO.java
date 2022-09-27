package com.example.weatherapplicationrestapi.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListDTO {
    private final List<CityDTO> listOfFavouriteCities;

}
