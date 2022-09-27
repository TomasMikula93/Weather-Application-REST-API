package com.example.weatherapplicationrestapi.Repositories;

import com.example.weatherapplicationrestapi.Models.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserListRepository extends JpaRepository<UserList, Long> {
    Optional<UserList> findByWaUser_UsernameAndId(String username, long id);
    Optional<UserList> findOptionalById(long id);
    UserList findById(long id);
}
