package com.example.weatherapplicationrestapi.Controllers;

import com.example.weatherapplicationrestapi.Filters.JwtRequestFilter;
import com.example.weatherapplicationrestapi.Models.City;
import com.example.weatherapplicationrestapi.Models.DTOs.ErrorMsgDTO;
import com.example.weatherapplicationrestapi.Models.DTOs.MessageDTO;
import com.example.weatherapplicationrestapi.Models.WAUser;
import com.example.weatherapplicationrestapi.Services.UserListService;
import com.example.weatherapplicationrestapi.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserListController {
    private final WebClient.Builder webClientBuilder;
    private final UserService userService;
    private final UserListService userListService;

    @PostMapping("/city/{id}")
    public ResponseEntity<Object> addFavouriteCity(@RequestBody City city, @RequestHeader(value = "Authorization") String token,
                                                   @PathVariable long id) {
        if (!userService.userOwnsList(JwtRequestFilter.username, id) || token.isEmpty()
                || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new ErrorMsgDTO("This List does not belong to authenticated player"));
        }
        if (!userListService.checkIfListExists(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new ErrorMsgDTO("This List does not exist"));
        }

        userListService.saveFavouriteCity(city, id);
        return ResponseEntity.status(200).body(new MessageDTO("City has been added to your list."));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> showList(@RequestHeader(value = "Authorization") String token) {
        if (token.isEmpty() || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new ErrorMsgDTO("Wrong or empty token"));
        }
        return ResponseEntity.status(200).body(userListService.makeListDTO(JwtRequestFilter.username));
    }

    @PostMapping("/weatherInCity/{id}")
    public ResponseEntity<Object> userRegistrationPost(@RequestBody City city, @RequestHeader(value = "Authorization") String token,
                                                       @PathVariable long id) {

        if (!userService.userOwnsList(JwtRequestFilter.username, id) || token.isEmpty()
                || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new ErrorMsgDTO("This List does not belong to authenticated player"));
        }
        if (!userListService.checkIfListExists(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new ErrorMsgDTO("This List does not exist"));
        }
        if (!userListService.checkIfCityExists(city.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new ErrorMsgDTO("This City does not exist"));
        }

        return ResponseEntity.status(200).body(userListService.showWeatherInMyFavouriteCity(city.getId()));

    }
//
//        String apiKey = System.getenv("API_KEY");
//        String responseBody = webClientBuilder.build()
//                .post()
//                //TODO by name of town and county code
//                .uri("https://api.openweathermap.org/data/2.5/weather?q=Říčany,cz&APPID=" + apiKey)
//                //TODO by coordinates lon + lat
////                .uri("https://api.openweathermap.org/data/2.5/weather?lat=" + city.getLatitude() +
////                        "&lon=" + city.getLongitude() + "&appid=" + apiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .onStatus(HttpStatus::isError, response -> Mono.empty())
//                .bodyToMono(String.class)
//                .block();
//
//        return ResponseEntity.status(200).body(new MessageDTO("good weather" + responseBody));


}
