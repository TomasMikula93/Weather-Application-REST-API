package com.example.weatherapplicationrestapi.Repositories;

import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.WAUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
