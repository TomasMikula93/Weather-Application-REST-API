package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.DTOs.ListDTO;
import com.example.weatherapplicationrestapi.Models.DTOs.WeatherDTO;

public interface UserListService {
    boolean checkIfListExists(long id);

    void saveFavouriteCity(City city, long id);

    ListDTO makeListDTO(String username);

    boolean checkIfCityExists(long idOfCity);

    WeatherDTO showWeatherInMyFavouriteCity(long id);
    String takeCountryCodeFromWeatherResponse(String message);
    String takeNameFromWeatherResponse(String message);
    String takeCelsiusFromWeatherResponse(String message);
}
