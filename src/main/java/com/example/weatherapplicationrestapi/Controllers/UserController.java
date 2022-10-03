package com.example.weatherapplicationrestapi.Controllers;

import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.DTOs.ErrorMsgDTO;
import com.example.weatherapplicationrestapi.Models.DTOs.MessageDTO;
import com.example.weatherapplicationrestapi.Models.DTOs.UserDTO;
import com.example.weatherapplicationrestapi.Models.WAUser;
import com.example.weatherapplicationrestapi.Registration.ConfirmationTokenService;
import com.example.weatherapplicationrestapi.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping("/registration")
    public ResponseEntity<Object> registration(@RequestBody WAUser user) {
        if (userService.checkIfUsernameExists(user.getUsername())) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("This username already exists!"));
        }
        if (userService.checkIfEmailExists(user.getEmail())) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("This email already exists!"));
        }
        if (user.getUsername().isEmpty() || user.getUsername().isBlank() || user.getUsername() == null) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Incorrect username."));
        }
        if (user.getPassword().isEmpty() || user.getPassword().isBlank() || user.getPassword() == null
                || user.getPassword().length() < 8) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Incorrect password."));
        }

        if (!userService.emailIsValidate(user.getEmail())) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Email is not valid"));

        }
        userService.saveNewUser(user);
        return ResponseEntity.status(200).body(new UserDTO(userService.findByUser(user).getId(), user.getUsername()));
    }

    @GetMapping(path = "registration/confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token) {
        if (confirmationTokenService
                .getTokenOptional(token).isEmpty()) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Token was not found!"));
        }
        if (confirmationTokenService.findToken(token).getConfirmedAt() != null) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Account is already confirmed"));
        }
        if (confirmationTokenService.findToken(token).getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Token is expired"));
        }
        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(
                confirmationTokenService.findToken(token).getWaUser().getUsername());
        return ResponseEntity.status(200).body(new MessageDTO("Thank you, your account is activated!"));
    }

    @GetMapping("registration/newToken")
    public ResponseEntity<Object> generateNewConfirmToken(@RequestBody WAUser wauser) {
        if (!userService.checkIfUsernameExists(wauser.getUsername())) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("This username doesn't exist!"));
        }
        if (!userService.emailIsValidate(wauser.getEmail())) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Email is not valid"));
        }
        if(!userService.emailMatches(wauser.getEmail(), wauser.getUsername())){
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Incorrect email for this username"));
        }
        if (userService.userAccountIsEnabled(wauser.getUsername())) {
            return ResponseEntity.status(400).body(new ErrorMsgDTO("Account is already activated"));
        }

        userService.generateNewToken(wauser.getUsername(), wauser.getEmail());
        return ResponseEntity.status(200).body(new MessageDTO("Your new confirmation token has been generated!"));
    }

}



