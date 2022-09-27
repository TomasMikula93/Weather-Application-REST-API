package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.City;

public interface UserListService {
    boolean checkIfListExists(long id);

    void saveFavouriteCity(City city, long id);
}
