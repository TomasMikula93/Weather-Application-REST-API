package com.example.weatherapplicationrestapi.Repositories;

import com.example.weatherapplicationrestapi.Models.WAUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<WAUser, Long> {

    Optional<WAUser> findOptionalByUsername(String username);
    WAUser findByUsername(String username);
}
