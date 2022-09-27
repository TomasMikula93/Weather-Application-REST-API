package com.example.weatherapplicationrestapi.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WAUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    private String email;

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<City> favouriteCities;


    public WAUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
