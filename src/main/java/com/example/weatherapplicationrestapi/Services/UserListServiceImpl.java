package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.DTOs.CityDTO;
import com.example.weatherapplicationrestapi.Models.DTOs.ListDTO;
import com.example.weatherapplicationrestapi.Models.UserList;
import com.example.weatherapplicationrestapi.Repositories.CityRepository;
import com.example.weatherapplicationrestapi.Repositories.UserListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserListServiceImpl implements UserListService {

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
            sublist.add(new CityDTO(city.getName(),
                    city.getLongitude(),
                    city.getLatitude()));
        }
        return new ListDTO(sublist);
    }
}
