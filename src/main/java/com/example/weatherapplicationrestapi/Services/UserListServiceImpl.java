package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.DTOs.CityDTO;
import com.example.weatherapplicationrestapi.Models.DTOs.ListDTO;
import com.example.weatherapplicationrestapi.Models.DTOs.WeatherDTO;
import com.example.weatherapplicationrestapi.Models.UserList;
import com.example.weatherapplicationrestapi.Repositories.CityRepository;
import com.example.weatherapplicationrestapi.Repositories.UserListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserListServiceImpl implements UserListService {
    private final WebClient.Builder webClientBuilder;
    private final UserListRepository userListRepository;
    private final CityRepository cityRepository;

    @Override
    public boolean checkIfListExists(long id) {
        return userListRepository.findOptionalById(id).isPresent();
    }

    @Override
    public void saveFavouriteCity(City city, long id) {
        City newCity = new City(city.getName(), city.getLongitude(), city.getLatitude());
        cityRepository.save(newCity);

        UserList sublist = userListRepository.findById(id);
        sublist.getFavouriteCities().add(newCity);
        newCity.setUserList(sublist);
        userListRepository.save(sublist);
        cityRepository.save(newCity);
    }

    @Override
    public ListDTO makeListDTO(String username) {
        List<City> list = userListRepository.findByWaUser_Username(username).getFavouriteCities();
        List<CityDTO> sublist = new ArrayList<>();

        for (City city : list) {
            sublist.add(new CityDTO(city.getId(),
                    city.getName(),
                    city.getLongitude(),
                    city.getLatitude()));
        }
        return new ListDTO(sublist);
    }

    @Override
    public boolean checkIfCityExists(long idOfCity) {
        return cityRepository.findOptionalById(idOfCity).isPresent();
    }

    @Override
    public WeatherDTO showWeatherInMyFavouriteCity(long id) {
        String apiKey = System.getenv("API_KEY");
        City city = cityRepository.findById(id);

        String responseBody = webClientBuilder.build()
                .post()
                .uri("https://api.openweathermap.org/data/2.5/weather?lat=" + city.getLatitude() +
                        "&lon=" + city.getLongitude() + "&appid=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.empty())
                .bodyToMono(String.class)
                .block();

        return new WeatherDTO(takeCountryCodeFromWeatherResponse(responseBody),
                takeNameFromWeatherResponse(responseBody),
                takeCelsiusFromWeatherResponse(responseBody) + " °C");
    }

    @Override
    public String takeCountryCodeFromWeatherResponse(String message) {
        String[] arr = message.split(",");
        String name = arr[20];
        String[] arr2 = name.split(":");
        String name2 = arr2[1];
        return name2.substring(1, name2.length() - 1);
    }

    @Override
    public String takeNameFromWeatherResponse(String message) {
        String[] arr = message.split(",");
        String name = arr[25];
        String[] arr2 = name.split(":");
        String name2 = arr2[1];
        return name2.substring(1, name2.length() - 1);
    }

    @Override
    public String takeCelsiusFromWeatherResponse(String message) {
        DecimalFormat df = new DecimalFormat("0.00");
        String[] arr = message.split(",");
        String name = arr[7];
        String[] arr2 = name.split(":");
        double kelvin = Double.parseDouble(arr2[2]);
        double celsius = kelvin - 273.15;
        return String.valueOf(df.format(celsius));
    }

    @Override
    public WeatherDTO showWeatherInLocation(double latitude, double longitude) {
        String apiKey = System.getenv("API_KEY");

        String responseBody = webClientBuilder.build()
                .post()
                .uri("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude +
                        "&lon=" + longitude + "&appid=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.empty())
                .bodyToMono(String.class)
                .block();

        return new WeatherDTO(takeCountryCodeFromWeatherResponse(responseBody),
                takeNameFromWeatherResponse(responseBody),
                takeCelsiusFromWeatherResponse(responseBody) + " °C");
    }
}
