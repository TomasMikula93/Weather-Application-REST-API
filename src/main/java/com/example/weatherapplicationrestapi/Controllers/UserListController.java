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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserListController {

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
}
