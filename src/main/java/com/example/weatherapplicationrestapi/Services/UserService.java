package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.WAUser;

public interface UserService {
    boolean checkIfUsernameExists(String username);

    boolean emailIsValidate(String email);

    void saveNewUser(WAUser user);

    void enableAppUser(String username);

    WAUser findByUser(WAUser user);

    boolean userOwnsList(String username, long id);

    void generateNewToken(String username, String email);

    boolean userAccountIsEnabled(String username);

    boolean emailMatches(String email, String username);
    boolean checkIfEmailExists(String email);

    boolean checkIfTokenExpired(WAUser wauser);
}
