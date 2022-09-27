package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.UserList;
import com.example.weatherapplicationrestapi.Repositories.CityRepository;
import com.example.weatherapplicationrestapi.Repositories.UserListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        userListRepository.save(sublist);
    }
}
