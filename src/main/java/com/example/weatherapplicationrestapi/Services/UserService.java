package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.WAUser;

public interface UserService {
    boolean checkIfUsernameExists(String username);

    boolean emailIsValidate(String email);

    void saveNewUser(WAUser user);
    void enableAppUser(String username);

    WAUser findByUser(WAUser user);
}
