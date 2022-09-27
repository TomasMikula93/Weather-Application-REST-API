package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.DTOs.ListDTO;

public interface UserListService {
    boolean checkIfListExists(long id);

    void saveFavouriteCity(City city, long id);

    ListDTO makeListDTO(String username);
}
