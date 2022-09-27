package com.example.weatherapplicationrestapi.Registration;

import java.util.Optional;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);
    Optional<ConfirmationToken> getTokenOptional(String token);
    ConfirmationToken findToken(String token);
    void setConfirmedAt(String token);
}
